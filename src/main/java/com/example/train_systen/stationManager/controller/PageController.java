package com.example.train_systen.stationManager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    /**
     * Handles requests for the home page.
     * @return the index.html template
     */
    @GetMapping("/")
    public String showHomePage() {
        return "index"; // This should match your index.html file name without the extension
    }

    /**
     * Handles requests for the attractions page.
     * @return the attractions.html template
     */
    @GetMapping("/attractions")
    public String showAttractionsPage() {
        return "attractions"; // This should match your attractions.html file name
    }

    /**
     * NOTE: You have a /login endpoint in your TicketController that takes a parameter.
     * If you want a generic login page, you might need to create a separate
     * controller or Thymeleaf template for it. For now, the links in the HTML
     * point to a simple login action.
     *
     * The other links like /routes, /schedules, /packages will also need
     * @GetMapping methods here or in other controllers once you create those pages.
     */
}