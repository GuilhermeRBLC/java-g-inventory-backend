package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.models.Product;
import com.guilhermerblc.inventory.models.ProductInput;
import com.guilhermerblc.inventory.models.ProductOutput;
import com.guilhermerblc.inventory.service.ProductInputService;
import com.guilhermerblc.inventory.service.ProductOutputService;
import com.guilhermerblc.inventory.service.ProductService;
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
@RequestMapping("/api/v1/product")
@Tag(name = "Product Controller", description = "An API that manipulates the products in database.")
public class ProductController {

    private final ProductService service;
    private final ProductInputService productInputService;
    private final ProductOutputService productOutputService;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_PRODUCTS')")
    @Operation(summary = "Get all products data.", description = "Retrieves all stored products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieves all products available."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_PRODUCTS')")
    @Operation(summary = "Get a product by ID.", description = "Return the data of a specific product based on it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The requested product data."),
            @ApiResponse(responseCode = "404", description = "The requested product is not found."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EDIT_PRODUCTS')")
    @Operation(summary = "Creates a new product.", description = "Creates a new product using the provided data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid data provided."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for create a product."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<Product> create(@Valid @RequestBody Product body) {
        return ResponseEntity.ok(service.crate(body));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDIT_PRODUCTS')")
    @Operation(summary = "Updates a product by ID.", description = "Updates a specific product by it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product update successfully."),
            @ApiResponse(responseCode = "404", description = "The requested product is not found."),
            @ApiResponse(responseCode = "422", description = "IDs must be the same in path and body."),
            @ApiResponse(responseCode = "400", description = "Invalid data provided."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for update the product."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody Product body) {
        return ResponseEntity.ok(service.update(id, body));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_PRODUCTS')")
    @Operation(summary = "Delete a product by ID.", description = "Delete a specific product by it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully."),
            @ApiResponse(responseCode = "404", description = "The requested product is not found."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for delete the product."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/inputs")
    @PreAuthorize("hasAuthority('VIEW_INPUTS')")
    @Operation(summary = "Get all inputs data of a product.", description = "Retrieves all inputs for a product specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieves all inputs for a product."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<List<ProductInput>> findAllInput(@PathVariable Long id) {
        return ResponseEntity.ok(productInputService.findByProductId(id));
    }

    @GetMapping("/{id}/outputs")
    @PreAuthorize("hasAuthority('VIEW_OUTPUTS')")
    @Operation(summary = "Get all output data of a product.", description = "Retrieves all output for a product specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieves all output for a product."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<List<ProductOutput>> findAllOutputs(@PathVariable Long id) {
        return ResponseEntity.ok(productOutputService.findByProductId(id));
    }


}
