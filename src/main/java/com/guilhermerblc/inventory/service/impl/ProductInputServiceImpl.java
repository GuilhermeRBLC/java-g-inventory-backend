package com.guilhermerblc.inventory.service.impl;

import com.guilhermerblc.inventory.models.ProductInput;
import com.guilhermerblc.inventory.models.User;
import com.guilhermerblc.inventory.repository.ProductInputRepository;
import com.guilhermerblc.inventory.service.ProductInputService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ProductInputServiceImpl implements ProductInputService {

    private final ProductInputRepository repository;

    @Override
    public List<ProductInput> findAll() {
        return repository.findAll();
    }

    @Override
    public List<ProductInput> findByProductId(Long productId) {
        return repository.findByProductId(productId);
    }

    @Override
    public ProductInput findById(Long id) {
        return repository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public ProductInput crate(ProductInput entity) {
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        entity.setUser(authenticatedUser);
        entity.setCreated(LocalDateTime.now());
        entity.setModified(null);
        return repository.save(entity);
    }

    @Override
    public ProductInput update(Long id, ProductInput entity) {
        ProductInput productInput = findById(id);

        if(!productInput.getId().equals(entity.getId())) {
            throw new RuntimeException("Update IDs must be the same.");
        }

        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        productInput.setProduct(entity.getProduct());
        productInput.setBarcode(entity.getBarcode());
        productInput.setSupplier(entity.getSupplier());
        productInput.setPurchaseValue(entity.getPurchaseValue());
        productInput.setPurchaseDate(entity.getPurchaseDate());
        productInput.setQuantity(entity.getQuantity());
        productInput.setObservations(entity.getObservations());
        productInput.setUser(authenticatedUser);
        productInput.setModified(LocalDateTime.now());

        return repository.save(productInput);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
