package ru.practicum.myblog5.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping
    public String home() {
        return PostController.REDIRECT_PATH;
    }
}
