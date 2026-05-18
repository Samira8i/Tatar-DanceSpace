package com.tatardancespace.controller.web;

import com.tatardancespace.entity.User;
import com.tatardancespace.service.CustomUserDetails;
import com.tatardancespace.service.CustomUserDetailsService;
import com.tatardancespace.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


//todo: вынести rest template в бин
//todo: вынести логику в контроллер
@Controller
public class OAuthController {

    private static final Logger log = LoggerFactory.getLogger(OAuthController.class);

    @Value("${oauth.yandex.client-id}")
    private String clientId;

    @Value("${oauth.yandex.client-secret}")
    private String clientSecret;

    @Value("${oauth.yandex.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    public OAuthController(UserService userService, CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/oauth2/authorization/yandex")
    public String redirectToYandex() {
        String url = String.format(
                "https://oauth.yandex.ru/authorize?response_type=code&client_id=%s&redirect_uri=%s",
                clientId, redirectUri
        );
        log.info("Redirecting to Yandex OAuth: {}", url);
        return "redirect:" + url;
    }

    @GetMapping("/login/oauth2/code/yandex")
    public String yandexCallback(@RequestParam("code") String code) {
        try {
            log.info("Received Yandex callback with code: {}", code);

            // 1. Получаем access_token
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("code", code);
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> tokenResponse = restTemplate.exchange(
                    "https://oauth.yandex.ru/token",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode tokenJson = mapper.readTree(tokenResponse.getBody());
            String accessToken = tokenJson.get("access_token").asText();

            log.info("Got access token");

            // 2. Получаем данные пользователя
            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(accessToken);
            HttpEntity<?> userRequest = new HttpEntity<>(userHeaders);

            ResponseEntity<String> userResponse = restTemplate.exchange(
                    "https://login.yandex.ru/info?format=json",
                    HttpMethod.GET,
                    userRequest,
                    String.class
            );

            JsonNode userJson = mapper.readTree(userResponse.getBody());

            String email = extractEmail(userJson);
            String username = extractUsername(userJson);

            log.info("Yandex user: email={}, name={}", email, username);

            // 3. Находим или создаем пользователя
            User user = userService.findOrCreateOAuthUser(email, username);

            // 4. Аутентифицируем
            authenticateUser(user);

            return "redirect:/";

        } catch (Exception e) {
            log.error("Yandex OAuth error: ", e);
            return "redirect:/login?error=oauth_failed";
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

    private void authenticateUser(User user) {
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
}