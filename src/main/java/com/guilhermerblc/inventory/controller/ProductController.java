package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.models.Product;
import com.guilhermerblc.inventory.models.ProductInput;
import com.guilhermerblc.inventory.models.ProductOutput;
import com.guilhermerblc.inventory.service.ProductInputService;
import com.guilhermerblc.inventory.service.ProductOutputService;
import com.guilhermerblc.inventory.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductService service;
    private final ProductInputService productInputService;
    private final ProductOutputService productOutputService;

    @GetMapping
    @Secured("VIEW_PRODUCTS")
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Secured("VIEW_PRODUCTS")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Secured("EDIT_PRODUCTS")
    public ResponseEntity<Product> create(@RequestBody Product body) {
        return ResponseEntity.ok(service.crate(body));
    }

    @PutMapping("/{id}")
    @Secured("EDIT_PRODUCTS")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product body) {
        return ResponseEntity.ok(service.update(id, body));
    }

    @DeleteMapping("/{id}")
    @Secured("DELETE_PRODUCTS")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/inputs")
    @Secured("VIEW_INPUTS")
    public ResponseEntity<List<ProductInput>> findAllInput(@PathVariable Long id) {
        return ResponseEntity.ok(productInputService.findByProductId(id));
    }

    @GetMapping("/{id}/outputs")
    @Secured("VIEW_OUTPUTS")
    public ResponseEntity<List<ProductOutput>> findAllOutputs(@PathVariable Long id) {
        return ResponseEntity.ok(productOutputService.findByProductId(id));
    }


}
