package com.yntec.idp.masters.service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "city_master")
public class CityMaster extends BaseAuditableEntity {

    @Id
    @UuidGenerator
    @Column(name = "reference_id", updatable = false, nullable = false)
    private UUID cityId;

    @Column(name = "city_name", nullable = false, length = 100)
    private String cityName;

    @Column(name = "city_code", length = 20)
    private String cityCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "state_id", nullable = false)
    @JsonBackReference
    private StateMaster state;

}
