package com.yntec.idp.masters.service.payload.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CityBulkRequest {

    @NotEmpty(message = "City list cannot be empty")
    @Valid
    private List<CityCreateRequest> cities;
}
