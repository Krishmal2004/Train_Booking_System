package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.Route;
import com.example.train_systen.stationManager.repository.RouteRepository;
import com.example.train_systen.stationManager.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    
    @Autowired
    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }
    
    @Override
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }
    
    @Override
    public Optional<Route> getRouteById(Long id) {
        return routeRepository.findById(id);
    }

    @Override
    public Route saveRoute(Route route) {
        // --- NEW: Automatically generate Route ID for new routes ---
        // We check if the primary key (id) is null, which means it's a new entity.
        if (route.getId() == null) {
            // Find the next sequence number by counting existing routes.
            long nextSequence = routeRepository.count() + 1;
            // Format the ID as "R-" followed by a three-digit number (e.g., R-001, R-012, R-123).
            String generatedId = String.format("R-%03d", nextSequence);
            route.setRouteId(generatedId);
        }
        return routeRepository.save(route);
    }
    
    @Override
    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }
    
    @Override
    public List<Route> getRoutesByStatus(String status) {
        return routeRepository.findByStatus(status);
    }
    
    @Override
    public List<Route> searchRoutesByLocation(String location) {
        return routeRepository.findByLocation(location);
    }
    
    @Override
    public boolean isRouteIdExists(String routeId) {
        return routeRepository.existsByRouteId(routeId);
    }
}