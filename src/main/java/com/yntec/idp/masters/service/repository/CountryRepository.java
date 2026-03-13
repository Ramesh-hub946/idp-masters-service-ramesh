package com.yntec.idp.masters.service.repository;

import com.yntec.idp.masters.service.entity.CountryMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CountryRepository extends JpaRepository<CountryMaster, UUID> {

    Optional<CountryMaster> findByCountryNameIgnoreCase(String countryName);

    Optional<CountryMaster> findByCountryCodeIgnoreCase(String countryCode);

    boolean existsByCountryNameIgnoreCase(String countryName);

    boolean existsByCountryCodeIgnoreCase(String countryCode);

    Page<CountryMaster> findByCountryNameContainingIgnoreCase(String name, Pageable pageable);

    Page<CountryMaster> findByCountryNameContainingIgnoreCaseAndStatus(
            String name, Boolean status, Pageable pageable
    );
}
