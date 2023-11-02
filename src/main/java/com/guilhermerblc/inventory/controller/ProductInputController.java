package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.models.ProductInput;
import com.guilhermerblc.inventory.service.ProductInputService;
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
@RequestMapping("/api/v1/product-input")
@Tag(name = "Product Input Controller", description = "An API that manipulates the inputs of products in database.")
public class ProductInputController {

    private final ProductInputService service;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_INPUTS')")
    @Operation(summary = "Get all product inputs data.", description = "Retrieves all stored inputs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieves all inputs available."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource.")
    })
    public ResponseEntity<List<ProductInput>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_INPUTS')")
    @Operation(summary = "Get a product inputs by ID.", description = "Return the data of a specific inputs based on it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The requested input data."),
            @ApiResponse(responseCode = "404", description = "The requested input is not found."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource.")
    })
    public ResponseEntity<ProductInput> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EDIT_INPUTS')")
    @Operation(summary = "Creates a new product input.", description = "Creates a new input using the provided data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Input created successfully."),
            @ApiResponse(responseCode = "422", description = "Invalid data provided."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for create a input.")
    })
    public ResponseEntity<ProductInput> create(@Valid @RequestBody ProductInput body) {
        return ResponseEntity.ok(service.crate(body));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDIT_INPUTS')")
    @Operation(summary = "Updates a product input by ID.", description = "Updates a specific input by it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Input updated successfully."),
            @ApiResponse(responseCode = "404", description = "The requested input is not found."),
            @ApiResponse(responseCode = "422", description = "Invalid data provided."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for update the input.")
    })
    public ResponseEntity<ProductInput> update(@PathVariable Long id, @Valid @RequestBody ProductInput body) {
        return ResponseEntity.ok(service.update(id, body));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_INPUTS')")
    @Operation(summary = "Delete a product input by ID.", description = "Delete a specific input by it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Input deleted successfully."),
            @ApiResponse(responseCode = "404", description = "The requested input is not found."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for delete the input.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
