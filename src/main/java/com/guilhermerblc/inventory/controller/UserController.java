package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.models.User;
import com.guilhermerblc.inventory.service.UserService;
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
@RequestMapping("/api/v1/user")
@Tag(name = "User Controller", description = "An API that manipulates the users in database.")
public class UserController {

    private final UserService service;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    @Operation(summary = "Get all users data.", description = "Retrieves all stored users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieves all users available."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    @Operation(summary = "Get a user by ID.", description = "Return the data of a specific user based on it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The requested user data."),
            @ApiResponse(responseCode = "404", description = "The requested user is not found."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EDIT_USERS')")
    @Operation(summary = "Creates a new user.", description = "Creates a new user using the provided data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid data provided."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for create a user."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<User> create(@Valid @RequestBody User body) {
        return ResponseEntity.ok(service.crate(body));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDIT_USERS')")
    @Operation(summary = "Updates a user by ID.", description = "Updates a specific user by it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully."),
            @ApiResponse(responseCode = "404", description = "The requested user is not found."),
            @ApiResponse(responseCode = "422", description = "IDs must be the same in path and body."),
            @ApiResponse(responseCode = "400", description = "Invalid data provided."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for update the user."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody User body) {
        return ResponseEntity.ok(service.update(id, body));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_USERS')")
    @Operation(summary = "Delete a user by ID.", description = "Delete a specific user by it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully."),
            @ApiResponse(responseCode = "404", description = "The requested user is not found."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for delete the user."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
