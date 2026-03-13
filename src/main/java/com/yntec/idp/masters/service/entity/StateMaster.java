package com.yntec.idp.masters.service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "state_master")
public class StateMaster extends BaseAuditableEntity {

    @Id
    @UuidGenerator
    @Column(name = "reference_id", updatable = false, nullable = false)
    private UUID stateId;

    @Column(name = "state_name", nullable = false, length = 100)
    private String stateName;

    @Column(name = "state_code", length = 10)
    private String stateCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    @JsonBackReference
    private CountryMaster country;

    @OneToMany(mappedBy = "state", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CityMaster> cities = new ArrayList<>();

}
