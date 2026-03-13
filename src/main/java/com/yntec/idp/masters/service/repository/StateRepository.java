package com.yntec.idp.masters.service.repository;

import com.yntec.idp.masters.service.entity.StateMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StateRepository extends JpaRepository<StateMaster, UUID> {

    List<StateMaster> findByCountryCountryId(UUID countryId);

    List<StateMaster> findByCountryCountryIdOrderByStateNameAsc(UUID countryId);

    Page<StateMaster> findByStateNameContainingIgnoreCase(String name, Pageable pageable);

    Page<StateMaster> findByStateNameContainingIgnoreCaseAndCountryCountryId(
            String name, UUID countryId, Pageable pageable
    );

    Optional<StateMaster> findByStateNameIgnoreCaseAndCountryCountryId(
            String stateName, UUID countryId
    );

    Optional<StateMaster> findByStateCodeIgnoreCaseAndCountryCountryId(
            String stateCode, UUID countryId
    );
}
