package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.models.Configuration;
import com.guilhermerblc.inventory.models.Permission;
import com.guilhermerblc.inventory.service.ConfigurationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@AllArgsConstructor
@Secured("EDIT_CONFIGURATIONS")
@RequestMapping("/api/v1/configuration")
public class ConfigurationController {

    private final ConfigurationService service;

    @GetMapping
    public ResponseEntity<List<Configuration>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Configuration> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Configuration> update(@PathVariable Long id, @RequestBody Configuration body) {
        return ResponseEntity.ok(service.update(id, body));
    }

}
