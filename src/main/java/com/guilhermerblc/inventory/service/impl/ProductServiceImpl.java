package com.guilhermerblc.inventory.service.impl;

import com.guilhermerblc.inventory.models.Product;
import com.guilhermerblc.inventory.repository.ProductRepository;
import com.guilhermerblc.inventory.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    @Override
    public List<Product> findAll() {
        return repository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    public Product crate(Product entity) {
        return repository.save(entity);
    }

    @Override
    public Product update(Long id, Product entity) {
        Product product = findById(id);

        product.setDescription(entity.getDescription());
        product.setType(entity.getType());
        product.setInventoryMinimum(entity.getInventoryMinimum());
        product.setInventoryMaximum(entity.getInventoryMaximum());
        product.setObservations(entity.getObservations());
        product.setUser(entity.getUser());
        product.setModified(LocalDateTime.now());

        return repository.save(product);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
