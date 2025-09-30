package com.example.train_systen.stationManager.repository;

import com.example.train_systen.stationManager.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    Optional<Package> findByPackageID(String packageID);

    boolean existsByPackageID(String packageID);

    List<Package> findByStatus(String status);

    List<Package> findByStartDate(LocalDate startDate);

    List<Package> findByRouteId(Long routeId);

    List<Package> findByStartDateBetweenOrEndDateBetween(
            LocalDate startDate1, LocalDate endDate1,
            LocalDate startDate2, LocalDate endDate2);
}