package com.yntec.idp.masters.service.controller;

import com.yntec.idp.masters.service.payload.request.*;
import com.yntec.idp.masters.service.payload.response.ApiResponse;
import com.yntec.idp.masters.service.payload.response.CityResponse;
import com.yntec.idp.masters.service.service.CityMasterService;
import com.yntec.idp.masters.service.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * City master endpoints.
 * These APIs manage city records and expose state-scoped city lookup.
 */
@RestController
@RequestMapping("/api/idp-masters-service")
public class CityMasterController {

        private final CityMasterService service;

        public CityMasterController(CityMasterService service) {
                this.service = service;
        }

        /**
         * Creates a city under a state.
         */
        @PostMapping("/cities")
        public ResponseEntity<ApiResponse<CityResponse>> createCity(
                        @Valid @RequestBody CityCreateRequest request) {

                CityResponse created = service.create(request);

                URI location = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(created.getCityId())
                                .toUri();

                return ResponseEntity.created(location)
                                .body(ResponseBuilder.success("City created successfully", created));
        }

        /**
         * Returns cities as plain list data.
         */
        @GetMapping("/cities")
        public ResponseEntity<ApiResponse<List<CityResponse>>> getAllCities() {

                List<CityResponse> cities = service.findAll();

                return ResponseEntity.ok(
                                ResponseBuilder.success("Cities fetched successfully", cities));
        }

        /**
         * Returns one city by UUID.
         */
        @GetMapping("/cities/{id}")
        public ResponseEntity<ApiResponse<CityResponse>> getCityById(
                        @PathVariable UUID id) {

                CityResponse response = service.findById(id);

                return ResponseEntity.ok(
                                ResponseBuilder.success("City fetched successfully", response));
        }

        /**
         * Searches cities by name and optional state filter.
         */
        @GetMapping("/cities/search")
        public ResponseEntity<ApiResponse<List<CityResponse>>> searchCities(
                        @RequestParam String name,
                        @RequestParam(required = false) UUID stateId,
                        Pageable pageable) {

                List<CityResponse> cities = service.search(name, stateId, pageable).getContent();

                return ResponseEntity.ok(
                                ResponseBuilder.success("Cities search completed successfully", cities));
        }

        /**
         * Updates city details.
         */
        @PutMapping("/cities/{id}")
        public ResponseEntity<ApiResponse<CityResponse>> updateCity(
                        @PathVariable UUID id,
                        @Valid @RequestBody CityUpdateRequest request) {

                CityResponse updated = service.update(id, request);

                return ResponseEntity.ok(
                                ResponseBuilder.success("City updated successfully", updated));
        }

        /**
         * Activates/deactivates a city.
         */
        @PutMapping("/cities/{id}/status")
        public ResponseEntity<ApiResponse<CityResponse>> updateCityStatus(
                        @PathVariable UUID id,
                        @RequestParam Boolean status) {

                CityResponse updated = service.updateStatus(id, status);

                return ResponseEntity.ok(
                                ResponseBuilder.success("City status updated successfully", updated));
        }

        /**
         * Creates multiple cities in one request.
         */
        @PostMapping("/cities/bulk")
        public ResponseEntity<ApiResponse<List<CityResponse>>> bulkCreateCities(
                        @Valid @RequestBody CityBulkRequest request) {

                List<CityResponse> responses = service.bulkCreate(request);

                return ResponseEntity.ok(
                                ResponseBuilder.success("Bulk cities processed successfully", responses));
        }

        /**
         * Returns cities for a given state.
         */
        @GetMapping("/cities-by-state/{stateId}")
        public ResponseEntity<ApiResponse<List<CityResponse>>> getCitiesByState(
                        @PathVariable UUID stateId) {

                List<CityResponse> cities = service.findByStateId(stateId);

                return ResponseEntity.ok(
                                ResponseBuilder.success("Cities fetched successfully", cities));
        }
}
