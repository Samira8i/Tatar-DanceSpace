package com.tatardancespace.controller.web;

import com.tatardancespace.entity.User;
import com.tatardancespace.service.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OAuthController {

    private static final Logger log = LoggerFactory.getLogger(OAuthController.class);
    private final OAuthService oauthService;

    public OAuthController(OAuthService oauthService) {
        this.oauthService = oauthService;
    }

    @GetMapping("/oauth2/authorization/yandex")
    public String redirectToYandex() {
        String url = oauthService.getRedirectUrl();
        log.info("Redirecting to Yandex OAuth: {}", url);
        return "redirect:" + url;
    }

    @GetMapping("/login/oauth2/code/yandex")
    public String yandexCallback(@RequestParam("code") String code) {
        try {
            User user = oauthService.processYandexCallback(code);
            oauthService.authenticateUser(user);
            return "redirect:/";
        } catch (Exception e) {
            log.error("Yandex OAuth error: ", e);
            return "redirect:/login?error=oauth_failed";
        }
    }
}