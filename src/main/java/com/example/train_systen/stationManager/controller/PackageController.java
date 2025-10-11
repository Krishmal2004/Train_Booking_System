package com.example.train_systen.stationManager.controller;

import com.example.train_systen.stationManager.model.Package;
import com.example.train_systen.stationManager.service.PackageService;
import com.example.train_systen.stationManager.service.RouteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Year;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/packages")
public class PackageController {

    private final PackageService packageService;
    private final RouteService routeService;

    @Autowired
    public PackageController(PackageService packageService, RouteService routeService) {
        this.packageService = packageService;
        this.routeService = routeService;
    }

    private void addCommonAttributes(Model model, HttpSession session) {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("currentDateTime", LocalDateTime.now());
    }

    @GetMapping
    public String listPackages(Model model, HttpSession session,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                               @RequestParam(required = false) Long routeId) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        List<Package> packages;
        if (status != null && !status.isEmpty()) {
            packages = packageService.getPackagesByStatus(status);
        } else if (startDate != null) {
            packages = packageService.getPackagesByStartDate(startDate);
        } else if (routeId != null) {
            packages = packageService.getPackagesByRouteId(routeId);
        } else {
            packages = packageService.getAllPackages();
        }

        model.addAttribute("packages", packages);
        addCommonAttributes(model, session);
        // CORRECTED PATH
        return "operationManager/package/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        model.addAttribute("package", new Package());
        model.addAttribute("routes", routeService.getAllRoutes());
        addCommonAttributes(model, session);
        // CORRECTED PATH
        return "operationManager/package/create";
    }

    @PostMapping("/create")
    public String createPackage(@Valid @ModelAttribute("package") Package pack,
                                BindingResult result,
                                @RequestParam(required = false) Long routeId,
                                RedirectAttributes redirectAttributes,
                                Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        if (packageService.isPackageIdExists(pack.getPackageID())) {
            result.rejectValue("packageID", "error.package", "Package ID already exists");
        }
        if (pack.getStartDate() != null && pack.getEndDate() != null &&
                pack.getStartDate().isAfter(pack.getEndDate())) {
            result.rejectValue("endDate", "error.package", "End date must be after start date");
        }
        if (result.hasErrors()) {
            model.addAttribute("routes", routeService.getAllRoutes());
            addCommonAttributes(model, session);
            // CORRECTED PATH
            return "operationManager/package/create";
        }
        if (routeId != null) {
            routeService.getRouteById(routeId).ifPresent(pack::setRoute);
        }
        packageService.savePackage(pack);
        redirectAttributes.addFlashAttribute("successMessage", "Seasonal package created successfully");
        return "redirect:/packages";
    }

    @GetMapping("/{id}")
    public String viewPackage(@PathVariable String id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        Package pack = packageService.getPackageById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid package ID: " + id));
        model.addAttribute("package", pack);
        addCommonAttributes(model, session);
        // CORRECTED PATH
        return "operationManager/package/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        Package pack = packageService.getPackageById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid package ID: " + id));
        model.addAttribute("package", pack);
        model.addAttribute("routes", routeService.getAllRoutes());
        addCommonAttributes(model, session);
        // CORRECTED PATH
        return "operationManager/package/edit";
    }

    @PostMapping("/{id}/edit")
    public String updatePackage(@PathVariable String id,
                                @Valid @ModelAttribute("package") Package pack,
                                BindingResult result,
                                @RequestParam(required = false) Long routeId,
                                RedirectAttributes redirectAttributes,
                                Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        if (pack.getStartDate() != null && pack.getEndDate() != null &&
                pack.getStartDate().isAfter(pack.getEndDate())) {
            result.rejectValue("endDate", "error.package", "End date must be after start date");
        }
        if (result.hasErrors()) {
            model.addAttribute("routes", routeService.getAllRoutes());
            addCommonAttributes(model, session);
            // CORRECTED PATH
            return "operationManager/package/edit";
        }
        if (routeId != null) {
            routeService.getRouteById(routeId).ifPresent(pack::setRoute);
        }
        packageService.savePackage(pack);
        redirectAttributes.addFlashAttribute("successMessage", "Seasonal package updated successfully");
        return "redirect:/packages";
    }

    @PostMapping("/{id}/delete")
    public String deletePackage(@PathVariable String id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        packageService.deletePackage(id);
        redirectAttributes.addFlashAttribute("successMessage", "Seasonal package deleted successfully");
        return "redirect:/packages";
    }

    @GetMapping("/report")
    public String showReportForm(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        model.addAttribute("routes", routeService.getAllRoutes());
        addCommonAttributes(model, session);
        // CORRECTED PATH
        return "operationManager/package/report";
    }

    @GetMapping("/generate-report")
    public String generateReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                 @RequestParam(required = false) Long routeId,
                                 Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        List<Package> packages = packageService.getPackagesByDateRange(startDate, endDate);
        if (routeId != null) {
            packages = packages.stream()
                    .filter(p -> p.getRoute() != null && p.getRoute().getId().equals(routeId))
                    .toList();
        }
        model.addAttribute("packages", packages);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("routeId", routeId);
        if (routeId != null) {
            routeService.getRouteById(routeId).ifPresent(route -> model.addAttribute("route", route));
        }
        addCommonAttributes(model, session);
        // CORRECTED PATH
        return "operationManager/package/report-result";
    }
}