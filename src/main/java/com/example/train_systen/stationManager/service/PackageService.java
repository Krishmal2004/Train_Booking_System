package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.Package;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PackageService {

    List<Package> getAllPackages();

    Optional<Package> getPackageById(String id);

    List<Package> getPackagesByStatus(String status);

    List<Package> getPackagesByStartDate(LocalDate startDate);

    List<Package> getPackagesByRouteId(Long routeId);

    List<Package> getPackagesByDateRange(LocalDate startDate, LocalDate endDate);

    boolean isPackageIdExists(String packageId);

    Package savePackage(Package pack);

    void deletePackage(String id);
}