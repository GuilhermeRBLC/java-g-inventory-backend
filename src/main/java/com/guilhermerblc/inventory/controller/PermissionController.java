package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.models.Permission;
import com.guilhermerblc.inventory.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@AllArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/v1/permission")
@Tag(name = "Permission Controller", description = "An API retrieves the permissions.")
public class PermissionController {

    private final PermissionService service;

    @GetMapping
    @Operation(summary = "Get all permissions data.", description = "Retrieves all stored permissions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieves all permissions data available."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource.")
    })
    public ResponseEntity<List<Permission>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a permission by ID.", description = "Return the data of a specific permission based on it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The requested permission data."),
            @ApiResponse(responseCode = "404", description = "The requested permission is not found."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource.")
    })
    public ResponseEntity<Permission> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

}
