package com.guilhermerblc.inventory.service;

import com.guilhermerblc.inventory.models.ProductOutput;

import java.util.List;

public interface ProductOutputService {

    List<ProductOutput> findAll();

    ProductOutput findById(Long id);

    ProductOutput crate(ProductOutput entity);

    ProductOutput update(Long id, ProductOutput entity);

    void delete(Long id);

}
