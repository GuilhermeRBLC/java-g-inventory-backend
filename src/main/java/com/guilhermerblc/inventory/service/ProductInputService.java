package com.guilhermerblc.inventory.service;

import com.guilhermerblc.inventory.models.ProductInput;

import java.util.List;

public interface ProductInputService {

    List<ProductInput> findAll();

    List<ProductInput> findByProductId(Long productId);

    ProductInput findById(Long id);

    ProductInput crate(ProductInput entity);

    ProductInput update(Long id, ProductInput entity);

    void delete(Long id);

}
