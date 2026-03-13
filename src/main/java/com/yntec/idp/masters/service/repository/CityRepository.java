package com.yntec.idp.masters.service.repository;

import com.yntec.idp.masters.service.entity.CityMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CityRepository extends JpaRepository<CityMaster, UUID> {

    List<CityMaster> findByStateStateId(UUID stateId);

    Page<CityMaster> findByCityNameContainingIgnoreCase(String name, Pageable pageable);

    Page<CityMaster> findByCityNameContainingIgnoreCaseAndStateStateId(
            String name, UUID stateId, Pageable pageable
    );

    boolean existsByCityNameIgnoreCaseAndStateStateId(String cityName, UUID stateId);

    boolean existsByCityCodeIgnoreCaseAndStateStateId(String cityCode, UUID stateId);

    Optional<CityMaster> findByCityNameIgnoreCaseAndStateStateId(
            String cityName, UUID stateId
    );

    Optional<CityMaster> findByCityCodeIgnoreCaseAndStateStateId(
            String cityCode, UUID stateId
    );
}
