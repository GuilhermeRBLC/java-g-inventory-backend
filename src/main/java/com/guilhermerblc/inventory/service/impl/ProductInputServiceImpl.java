package com.guilhermerblc.inventory.service.impl;

import com.guilhermerblc.inventory.models.ProductInput;
import com.guilhermerblc.inventory.repository.ProductInputRepository;
import com.guilhermerblc.inventory.service.ProductInputService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductInputServiceImpl implements ProductInputService {

    private final ProductInputRepository repository;

    @Override
    public List<ProductInput> findAll() {
        return repository.findAll();
    }

    @Override
    public ProductInput findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    public ProductInput crate(ProductInput entity) {
        return repository.save(entity);
    }

    @Override
    public ProductInput update(Long id, ProductInput entity) {
        ProductInput productInput = findById(id);

        productInput.setProduct(entity.getProduct());
        productInput.setBarcode(entity.getBarcode());
        productInput.setSupplier(entity.getSupplier());
        productInput.setPurchaseValue(entity.getPurchaseValue());
        productInput.setPurchaseDate(entity.getPurchaseDate());
        productInput.setObservations(entity.getObservations());
        productInput.setUser(entity.getUser());
        productInput.setModified(LocalDateTime.now());

        return repository.save(productInput);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
