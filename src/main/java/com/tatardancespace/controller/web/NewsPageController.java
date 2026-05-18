package com.tatardancespace.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/news")
public class NewsPageController {

    @GetMapping
    public String newsPage() {
        return "news/list";
    }
}