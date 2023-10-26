package com.guilhermerblc.inventory.service;

import com.guilhermerblc.inventory.models.Product;

import java.util.List;

public interface ProductService {

    List<Product> findAll();

    Product findById(Long id);

    Product crate(Product entity);

    Product update(Long id, Product entity);

    void delete(Long id);

}
