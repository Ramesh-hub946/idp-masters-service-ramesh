package com.yntec.idp.masters.service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "country_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountryMaster extends BaseAuditableEntity {

    @Id
    @UuidGenerator
    @Column(name = "reference_id", updatable = false, nullable = false)
    private UUID countryId;

    @Column(name = "country_name", nullable = false, length = 100)
    private String countryName;

    @Column(name = "country_code", nullable = false, length = 3, unique = true)
    private String countryCode;

    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<StateMaster> states = new ArrayList<>();
}
