package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.models.Permission;
import com.guilhermerblc.inventory.service.PermissionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/permission")
public class PermissionController {

    private final PermissionService service;

    @GetMapping
    public ResponseEntity<List<Permission>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permission> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

}
