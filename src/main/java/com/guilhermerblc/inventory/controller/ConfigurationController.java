package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.models.Configuration;
import com.guilhermerblc.inventory.models.Permission;
import com.guilhermerblc.inventory.service.ConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@AllArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/v1/configuration")
@Tag(name = "Configuration Controller", description = "An API that manipulates some business specific data.")
public class ConfigurationController {

    private final ConfigurationService service;

    @GetMapping
    @Operation(summary = "Get all configurations data.", description = "Retrieves all stored configurations data. Currently the company name, logo, and email for notification.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieves all configurations data available."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource.")
    })
    public ResponseEntity<List<Configuration>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a configuration by ID.", description = "Return the data of a specific configuration based on it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The requested configuration data."),
            @ApiResponse(responseCode = "404", description = "The requested configuration is not found."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource.")
    })
    public ResponseEntity<Configuration> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDIT_CONFIGURATIONS')")
    @Operation(summary = "Updates a configuration by ID.", description = "Updates a specific configuration by it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration update successfully."),
            @ApiResponse(responseCode = "404", description = "The requested configuration is not found."),
            @ApiResponse(responseCode = "422", description = "Invalid data provided."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for update the configuration.")
    })
    public ResponseEntity<Configuration> update(@PathVariable Long id, @Valid @RequestBody Configuration body) {
        return ResponseEntity.ok(service.update(id, body));
    }

}
