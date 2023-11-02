package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.models.ProductOutput;
import com.guilhermerblc.inventory.service.ProductOutputService;
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
@RequestMapping("/api/v1/product-output")
@Tag(name = "Product Output Controller", description = "An API that manipulates the outputs of products in database.")
public class ProductOutputController {

    private final ProductOutputService service;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_OUTPUTS')")
    @Operation(summary = "Get all product outputs data.", description = "Retrieves all stored outputs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieves all outputs available."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource.")
    })
    public ResponseEntity<List<ProductOutput>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_OUTPUTS')")
    @Operation(summary = "Get a product outputs by ID.", description = "Return the data of a specific outputs based on it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The requested output data."),
            @ApiResponse(responseCode = "404", description = "The requested inputs is not found."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource.")
    })
    public ResponseEntity<ProductOutput> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EDIT_OUTPUTS')")
    @Operation(summary = "Creates a new product output.", description = "Creates a new output using the provided data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Output created successfully."),
            @ApiResponse(responseCode = "422", description = "Invalid data provided."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for create a output.")
    })
    public ResponseEntity<ProductOutput> create(@Valid @RequestBody ProductOutput body) {
        return ResponseEntity.ok(service.crate(body));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDIT_OUTPUTS')")
    @Operation(summary = "Updates a product output by ID.", description = "Updates a specific output by it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Output updated successfully."),
            @ApiResponse(responseCode = "404", description = "The requested input is not found."),
            @ApiResponse(responseCode = "422", description = "Invalid data provided."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for update the output.")
    })
    public ResponseEntity<ProductOutput> update(@PathVariable Long id, @Valid @RequestBody ProductOutput body) {
        return ResponseEntity.ok(service.update(id, body));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_OUTPUTS')")
    @Operation(summary = "Delete a product output by ID.", description = "Delete a specific output by it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Output deleted successfully."),
            @ApiResponse(responseCode = "404", description = "The requested output is not found."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for delete the output.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
