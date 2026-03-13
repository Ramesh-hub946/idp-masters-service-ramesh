package com.yntec.idp.masters.service.controller;

import com.yntec.idp.masters.service.payload.request.*;
import com.yntec.idp.masters.service.payload.response.*;
import com.yntec.idp.masters.service.service.CountryMasterService;
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
 * Country master endpoints.
 * These APIs create/update/read country records and expose country-linked
 * location views.
 */
@RestController
@RequestMapping("/api/idp-masters-service")
public class CountryMasterController {

        private final CountryMasterService service;

        public CountryMasterController(CountryMasterService service) {
                this.service = service;
        }

        /**
         * Creates a new country master entry.
         */
        @PostMapping("/countries")
        public ResponseEntity<ApiResponse<CountryResponse>> createCountry(
                        @Valid @RequestBody CountryCreateRequest request) {

                CountryResponse created = service.create(request);

                URI location = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(created.getCountryId())
                                .toUri();

                return ResponseEntity.created(location)
                                .body(ResponseBuilder.success("Country created successfully", created));
        }

        /**
         * Returns all countries as plain list data.
         */
        @GetMapping("/countries")
        public ResponseEntity<ApiResponse<List<CountryResponse>>> getAllCountries() {

                List<CountryResponse> countries = service.findAll();

                return ResponseEntity.ok(
                                ResponseBuilder.success("Countries fetched successfully", countries));
        }

        /**
         * Returns one country by UUID.
         */
        @GetMapping("/countries/{id}")
        public ResponseEntity<ApiResponse<CountryResponse>> getCountryById(
                        @PathVariable UUID id) {

                CountryResponse response = service.findById(id);

                return ResponseEntity.ok(
                                ResponseBuilder.success("Country fetched successfully", response));
        }

        /**
         * Searches countries by name and optional active/inactive status.
         */
        @GetMapping("/countries/search")
        public ResponseEntity<ApiResponse<List<CountryResponse>>> searchCountries(
                        @RequestParam String name,
                        @RequestParam(required = false) Boolean status,
                        Pageable pageable) {

                List<CountryResponse> countries = service.search(name, status, pageable).getContent();

                return ResponseEntity.ok(
                                ResponseBuilder.success("Countries search completed successfully", countries));
        }

        /**
         * Updates mutable country details.
         */
        @PutMapping("/countries/{id}")
        public ResponseEntity<ApiResponse<CountryResponse>> updateCountry(
                        @PathVariable UUID id,
                        @Valid @RequestBody CountryUpdateRequest request) {

                CountryResponse updated = service.update(id, request);

                return ResponseEntity.ok(
                                ResponseBuilder.success("Country updated successfully", updated));
        }

        /**
         * Activates/deactivates a country record.
         */
        @PutMapping("/countries/{id}/status")
        public ResponseEntity<ApiResponse<CountryResponse>> updateCountryStatus(
                        @PathVariable UUID id,
                        @RequestParam Boolean status) {

                CountryResponse updated = service.updateStatus(id, status);

                return ResponseEntity.ok(
                                ResponseBuilder.success("Country status updated successfully", updated));
        }

        /**
         * Creates multiple countries in one request.
         */
        @PostMapping("/countries/bulk")
        public ResponseEntity<ApiResponse<List<CountryResponse>>> bulkCreateCountries(
                        @Valid @RequestBody CountryBulkRequest request) {

                List<CountryResponse> responses = service.bulkCreate(request);

                return ResponseEntity.ok(
                                ResponseBuilder.success("Bulk countries processed successfully", responses));
        }

        /**
         * Returns country->state->city hierarchy for tree views.
         */
        @GetMapping("/location-tree")
        public ResponseEntity<ApiResponse<List<LocationTreeCountryResponse>>> getLocationTree() {

                List<LocationTreeCountryResponse> tree = service.getLocationTree();

                return ResponseEntity.ok(
                                ResponseBuilder.success("Location tree fetched successfully", tree));
        }

        /**
         * Returns location options prepared for dropdown use.
         */
        @GetMapping("/dropdown")
        public ResponseEntity<ApiResponse<List<DropdownCountryResponse>>> getDropdown() {

                List<DropdownCountryResponse> response = service.getDropdownData();

                return ResponseEntity.ok(
                                ResponseBuilder.success("Dropdown data fetched successfully", response));
        }

        /**
         * Returns city with its parent state and country details.
         */
        @GetMapping("/location/city/{id}")
        public ResponseEntity<ApiResponse<CityHierarchyResponse>> getCityHierarchy(
                        @PathVariable UUID id) {

                CityHierarchyResponse response = service.getCityWithHierarchy(id);

                return ResponseEntity.ok(
                                ResponseBuilder.success("City hierarchy fetched successfully", response));
        }
}
