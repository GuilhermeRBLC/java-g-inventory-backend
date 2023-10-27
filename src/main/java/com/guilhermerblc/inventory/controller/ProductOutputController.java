package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.models.ProductOutput;
import com.guilhermerblc.inventory.service.ProductOutputService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/product-output")
public class ProductOutputController {

    private final ProductOutputService service;

    @GetMapping
    public ResponseEntity<List<ProductOutput>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductOutput> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductOutput> create(@RequestBody ProductOutput body) {
        return ResponseEntity.ok(service.crate(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductOutput> update(@PathVariable Long id, @RequestBody ProductOutput body) {
        return ResponseEntity.ok(service.update(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
