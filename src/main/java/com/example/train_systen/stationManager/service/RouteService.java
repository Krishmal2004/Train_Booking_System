package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.Route;

import java.util.List;
import java.util.Optional;

public interface RouteService {

    List<Route> getAllRoutes();
    
    Optional<Route> getRouteById(Long id);
    
    Route saveRoute(Route route);
    
    void deleteRoute(Long id);
    
    List<Route> getRoutesByStatus(String status);
    
    List<Route> searchRoutesByLocation(String location);
    
    boolean isRouteIdExists(String routeId);
}