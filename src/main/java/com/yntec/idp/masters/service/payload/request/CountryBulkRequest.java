package com.yntec.idp.masters.service.payload.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountryBulkRequest {

    @NotEmpty(message = "Country list cannot be empty")
    @Valid
    private List<CountryCreateRequest> countries;
}
