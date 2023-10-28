package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.models.Report;
import com.guilhermerblc.inventory.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@AllArgsConstructor
@Secured("GENERATE_REPORTS")
@RequestMapping("/api/v1/report")
public class ReportController {

    private final ReportService service;

    @GetMapping
    public ResponseEntity<List<Report>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Report> create(@RequestBody Report body) {
        return ResponseEntity.ok(service.crate(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Report> update(@PathVariable Long id, @RequestBody Report body) {
        return ResponseEntity.ok(service.update(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
