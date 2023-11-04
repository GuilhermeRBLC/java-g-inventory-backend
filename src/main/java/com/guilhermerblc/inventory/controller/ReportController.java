package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.models.Report;
import com.guilhermerblc.inventory.service.ReportService;
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
@PreAuthorize("hasAuthority('GENERATE_REPORTS')")
@RequestMapping("/api/v1/report")
@Tag(name = "Report Controller", description = "An API that manipulates the reports in database.")
public class ReportController {

    private final ReportService service;

    @GetMapping
    @Operation(summary = "Get all reports data.", description = "Retrieves all stored reports.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieves all reports available."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<List<Report>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a report by ID.", description = "Return the data of a specific report based on it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The requested report data."),
            @ApiResponse(responseCode = "404", description = "The requested report is not found."),
            @ApiResponse(responseCode = "403", description = "You should be authenticated to access this resource."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<Report> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Creates a new report.", description = "Creates a new report using the provided data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid data provided."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for create a report."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<Report> create(@Valid @RequestBody Report body) {
        return ResponseEntity.ok(service.crate(body));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates a report by ID.", description = "Updates a specific report by it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report updated successfully."),
            @ApiResponse(responseCode = "404", description = "The requested report is not found."),
            @ApiResponse(responseCode = "422", description = "IDs must be the same in path and body."),
            @ApiResponse(responseCode = "400", description = "Invalid data provided."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for update the report."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<Report> update(@PathVariable Long id, @Valid @RequestBody Report body) {
        return ResponseEntity.ok(service.update(id, body));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a report by ID.", description = "Delete a specific report by it's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Report deleted successfully."),
            @ApiResponse(responseCode = "404", description = "The requested report is not found."),
            @ApiResponse(responseCode = "403", description = "The current user has no permission for delete the report."),
            @ApiResponse(responseCode = "401", description = "Token expired.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
