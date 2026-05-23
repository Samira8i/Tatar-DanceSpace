package com.tatardancespace.controller.web;

import com.tatardancespace.service.DanceHallService;
import com.tatardancespace.service.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final NewsService newsService;
    private final DanceHallService danceHallService;

    public HomeController(NewsService newsService, DanceHallService danceHallService) {
        this.newsService = newsService;
        this.danceHallService = danceHallService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("news", newsService.getDanceNews());
        model.addAttribute("topRatedHalls", danceHallService.getTopRatedHallsWithRating());
        return "home";
    }
}