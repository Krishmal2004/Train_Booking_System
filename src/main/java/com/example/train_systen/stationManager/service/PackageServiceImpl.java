package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.Package;
import com.example.train_systen.stationManager.repository.PackageRepository;
import com.example.train_systen.stationManager.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;

    @Autowired
    public PackageServiceImpl(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    @Override
    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    @Override
    public Optional<Package> getPackageById(String id) {
        return packageRepository.findByPackageID(id);
    }

    @Override
    public List<Package> getPackagesByStatus(String status) {
        return packageRepository.findByStatus(status);
    }

    @Override
    public List<Package> getPackagesByStartDate(LocalDate startDate) {
        return packageRepository.findByStartDate(startDate);
    }

    @Override
    public List<Package> getPackagesByRouteId(Long routeId) {
        return packageRepository.findByRouteId(routeId);
    }

    @Override
    public List<Package> getPackagesByDateRange(LocalDate startDate, LocalDate endDate) {
        return packageRepository.findByStartDateBetweenOrEndDateBetween(startDate, endDate, startDate, endDate);
    }

    @Override
    public boolean isPackageIdExists(String packageId) {
        return packageRepository.existsByPackageID(packageId);
    }

    @Override
    public Package savePackage(Package pack) {
        // --- MODIFIED: Automatically generate Package ID for new packages ---
        if (pack.getId() == null && (pack.getPackageID() == null || pack.getPackageID().isEmpty())) {
            int year = Year.now().getValue();
            // Get the count of all packages to create a simple sequence.
            // For a more robust solution, you might query for the max sequence number for the current year.
            long sequence = packageRepository.count() + 1;
            String generatedId = String.format("PKG-%d-%04d", year, sequence);
            pack.setPackageID(generatedId);
        }
        return packageRepository.save(pack);
    }

    @Override
    public void deletePackage(String id) {
        Optional<Package> packageOpt = packageRepository.findByPackageID(id);
        packageOpt.ifPresent(packageRepository::delete);
    }
}