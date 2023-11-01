package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.models.ProductInput;
import com.guilhermerblc.inventory.service.ProductInputService;
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
public class ProductInputController {

    private final ProductInputService service;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_INPUTS')")
    public ResponseEntity<List<ProductInput>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_INPUTS')")
    public ResponseEntity<ProductInput> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EDIT_INPUTS')")
    public ResponseEntity<ProductInput> create(@RequestBody ProductInput body) {
        return ResponseEntity.ok(service.crate(body));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDIT_INPUTS')")
    public ResponseEntity<ProductInput> update(@PathVariable Long id, @RequestBody ProductInput body) {
        return ResponseEntity.ok(service.update(id, body));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_INPUTS')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
