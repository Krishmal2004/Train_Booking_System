package com.example.train_systen.stationManager.controller;

import com.example.train_systen.stationManager.model.Route;
import com.example.train_systen.stationManager.service.RouteService; // Import the service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class pageControllerRoute {

    private final RouteService routeService;

    // 1. Inject the RouteService
    @Autowired
    public pageControllerRoute(RouteService routeService) {
        this.routeService = routeService;
    }

    // 2. Fix URL mapping from "/route" to "/routes"
    @GetMapping("/route")
    public String showRoutesPage(Model model) {
        // 3. Get routes from the database via the service
        List<Route> routes = routeService.getAllRoutes();

        // 4. Add the list to the model
        model.addAttribute("routes", routes);

        // 5. Return the name of the HTML file
        return "routes";
    }
}