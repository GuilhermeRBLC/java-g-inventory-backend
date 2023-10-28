package com.guilhermerblc.inventory.service.impl;

import com.guilhermerblc.inventory.models.ProductOutput;
import com.guilhermerblc.inventory.models.User;
import com.guilhermerblc.inventory.repository.ProductOutputRepository;
import com.guilhermerblc.inventory.service.ProductOutputService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductOutputServiceImpl implements ProductOutputService {

    private final ProductOutputRepository repository;

    @Override
    public List<ProductOutput> findAll() {
        return repository.findAll();
    }

    @Override
    public List<ProductOutput> findByProductId(Long productId) {
        return repository.findByProductId(productId);
    }

    @Override
    public ProductOutput findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    public ProductOutput crate(ProductOutput entity) {
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        entity.setUser(authenticatedUser);
        entity.setCreated(LocalDateTime.now());
        entity.setModified(null);
        return repository.save(entity);
    }

    @Override
    public ProductOutput update(Long id, ProductOutput entity) {
        ProductOutput productOutput = findById(id);
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        productOutput.setProduct(entity.getProduct());
        productOutput.setBarcode(entity.getBarcode());
        productOutput.setBuyer(entity.getBuyer());
        productOutput.setSaleValue(entity.getSaleValue());
        productOutput.setSaleDate(entity.getSaleDate());
        productOutput.setQuantity(entity.getQuantity());
        productOutput.setObservations(entity.getObservations());
        productOutput.setUser(authenticatedUser);
        productOutput.setModified(LocalDateTime.now());

        return repository.save(productOutput);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
