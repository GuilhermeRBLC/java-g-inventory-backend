package com.guilhermerblc.inventory.service.impl;

import com.guilhermerblc.inventory.exceptions.IdentificationNotEqualsException;
import com.guilhermerblc.inventory.models.Product;
import com.guilhermerblc.inventory.models.User;
import com.guilhermerblc.inventory.repository.ProductRepository;
import com.guilhermerblc.inventory.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

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
        return repository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Product crate(Product entity) {
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        entity.setUser(authenticatedUser);
        entity.setCreated(LocalDateTime.now());
        entity.setModified(null);
        return repository.save(entity);
    }

    @Override
    public Product update(Long id, Product entity) {
        Product product = findById(id);

        if(!product.getId().equals(entity.getId())) {
            throw new IdentificationNotEqualsException("Update IDs must be the same.");
        }

        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        product.setDescription(entity.getDescription());
        product.setType(entity.getType());
        product.setInventoryMinimum(entity.getInventoryMinimum());
        product.setInventoryMaximum(entity.getInventoryMaximum());
        product.setObservations(entity.getObservations());
        product.setUser(authenticatedUser);
        product.setModified(LocalDateTime.now());

        return repository.save(product);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
