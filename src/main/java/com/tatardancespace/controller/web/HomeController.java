package com.tatardancespace.controller.web;

import com.tatardancespace.service.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final NewsService newsService;

    public HomeController(NewsService newsService) {
        this.newsService = newsService;
    }
    // todo: спросить - проверку на аутентификацию и показ новостец лучше в контроллере или во view
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("news", newsService.getDanceNews());
        return "home";
    }
}