package com.yntec.idp.masters.service.controller;

import com.yntec.idp.masters.service.payload.request.*;
import com.yntec.idp.masters.service.payload.response.ApiResponse;
import com.yntec.idp.masters.service.payload.response.StateResponse;
import com.yntec.idp.masters.service.service.StateMasterService;
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
 * State master endpoints.
 * These APIs manage state records and keep responses list-focused for UI
 * consumption.
 */
@RestController
@RequestMapping("/api/idp-masters-service")
public class StateMasterController {

        private final StateMasterService service;

        public StateMasterController(StateMasterService service) {
                this.service = service;
        }

        /**
         * Creates a state under a country.
         */
        @PostMapping("/states")
        public ResponseEntity<ApiResponse<StateResponse>> createState(
                        @Valid @RequestBody StateCreateRequest request) {

                StateResponse created = service.create(request);

                URI location = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(created.getStateId())
                                .toUri();

                return ResponseEntity.created(location)
                                .body(ResponseBuilder.success("State created successfully", created));
        }

        /**
         * Returns states as plain list data.
         */
        @GetMapping("/states")
        public ResponseEntity<ApiResponse<List<StateResponse>>> getAllStates() {

                List<StateResponse> states = service.findAll();

                return ResponseEntity.ok(
                                ResponseBuilder.success("States fetched successfully", states));
        }

        /**
         * Returns one state by UUID.
         */
        @GetMapping("/states/{id}")
        public ResponseEntity<ApiResponse<StateResponse>> getStateById(
                        @PathVariable UUID id) {

                StateResponse response = service.findById(id);

                return ResponseEntity.ok(
                                ResponseBuilder.success("State fetched successfully", response));
        }

        /**
         * Returns states for a country.
         * This endpoint exists only here to avoid duplicate handler mapping conflicts.
         */
        @GetMapping("/states-by-country/{countryId}")
        public ResponseEntity<ApiResponse<List<StateResponse>>> getStatesByCountry(
                        @PathVariable UUID countryId) {

                List<StateResponse> states = service.findByCountryId(countryId);

                return ResponseEntity.ok(
                                ResponseBuilder.success("States fetched successfully", states));
        }

        /**
         * Searches states by name and optional country filter.
         */
        @GetMapping("/states/search")
        public ResponseEntity<ApiResponse<List<StateResponse>>> searchStates(
                        @RequestParam String name,
                        @RequestParam(required = false) UUID countryId,
                        Pageable pageable) {

                List<StateResponse> states = service.search(name, countryId, pageable).getContent();

                return ResponseEntity.ok(
                                ResponseBuilder.success("States search completed successfully", states));
        }

        /**
         * Updates state details.
         */
        @PutMapping("/states/{id}")
        public ResponseEntity<ApiResponse<StateResponse>> updateState(
                        @PathVariable UUID id,
                        @Valid @RequestBody StateUpdateRequest request) {

                StateResponse updated = service.update(id, request);

                return ResponseEntity.ok(
                                ResponseBuilder.success("State updated successfully", updated));
        }

        /**
         * Activates/deactivates a state.
         */
        @PutMapping("/states/{id}/status")
        public ResponseEntity<ApiResponse<StateResponse>> updateStateStatus(
                        @PathVariable UUID id,
                        @RequestParam Boolean status) {

                StateResponse updated = service.updateStatus(id, status);

                return ResponseEntity.ok(
                                ResponseBuilder.success("State status updated successfully", updated));
        }

        /**
         * Creates multiple states in one request.
         */
        @PostMapping("/states/bulk")
        public ResponseEntity<ApiResponse<List<StateResponse>>> bulkCreateStates(
                        @Valid @RequestBody StateBulkRequest request) {

                List<StateResponse> responses = service.bulkCreate(request);

                return ResponseEntity.ok(
                                ResponseBuilder.success("Bulk states processed successfully", responses));
        }
}
