package com.guilhermerblc.inventory.repository;

import com.guilhermerblc.inventory.models.ProductInput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductInputRepository extends JpaRepository<ProductInput, Long> {

    List<ProductInput> findByProductId(Long id);

}
