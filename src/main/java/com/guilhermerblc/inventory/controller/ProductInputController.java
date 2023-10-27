package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.models.ProductInput;
import com.guilhermerblc.inventory.service.ProductInputService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/product-input")
public class ProductInputController {

    private final ProductInputService service;

    @GetMapping
    public ResponseEntity<List<ProductInput>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductInput> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductInput> create(@RequestBody ProductInput body) {
        return ResponseEntity.ok(service.crate(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductInput> update(@PathVariable Long id, @RequestBody ProductInput body) {
        return ResponseEntity.ok(service.update(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
