package com.tatardancespace.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tatardancespace.entity.User;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class OAuthService {

    private static final Logger log = LoggerFactory.getLogger(OAuthService.class);

    @Value("${oauth.yandex.client-id}")
    private String clientId;

    @Value("${oauth.yandex.client-secret}")
    private String clientSecret;

    @Value("${oauth.yandex.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    public OAuthService(RestTemplate restTemplate, ObjectMapper objectMapper,
                        UserService userService, CustomUserDetailsService customUserDetailsService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
    }

    public String getRedirectUrl() {
        return String.format(
                "https://oauth.yandex.ru/authorize?response_type=code&client_id=%s&redirect_uri=%s",
                clientId, redirectUri
        );
    }

    public User processYandexCallback(String code) {
        try {
            String accessToken = exchangeCodeForToken(code);
            JsonNode userJson = fetchUserInfo(accessToken);
            String email = extractEmail(userJson);
            String username = extractUsername(userJson);
            return userService.findOrCreateOAuthUser(email, username);
        } catch (Exception e) {
            log.error("Yandex OAuth error: ", e);
            throw new RuntimeException("OAuth authentication failed", e);
        }
    }
    public void authenticateUser(User user) {
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(user.getEmail());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

        log.info("User {} authenticated via Yandex", user.getEmail());
    }

    private String exchangeCodeForToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://oauth.yandex.ru/token",
                HttpMethod.POST,
                request,
                String.class
        );

        try {
            JsonNode tokenJson = objectMapper.readTree(response.getBody());
            return tokenJson.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse token response", e);
        }
    }

    private JsonNode fetchUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://login.yandex.ru/info?format=json",
                HttpMethod.GET,
                request,
                String.class
        );

        try {
            return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user info", e);
        }
    }

    private String extractEmail(JsonNode userJson) {
        if (userJson.has("default_email") && !userJson.get("default_email").isNull()) {
            return userJson.get("default_email").asText();
        }
        if (userJson.has("real_email") && !userJson.get("real_email").isNull()) {
            return userJson.get("real_email").asText();
        }
        return userJson.get("id").asText() + "@yandex.user";
    }

    private String extractUsername(JsonNode userJson) {
        if (userJson.has("display_name") && !userJson.get("display_name").isNull()) {
            return userJson.get("display_name").asText();
        }
        if (userJson.has("first_name") && !userJson.get("first_name").isNull()) {
            String firstName = userJson.get("first_name").asText();
            if (userJson.has("last_name") && !userJson.get("last_name").isNull()) {
                return firstName + " " + userJson.get("last_name").asText();
            }
            return firstName;
        }
        if (userJson.has("login") && !userJson.get("login").isNull()) {
            return userJson.get("login").asText();
        }
        return "Танцор";
    }
}