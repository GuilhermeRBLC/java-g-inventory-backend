package com.guilhermerblc.inventory.repository;

import com.guilhermerblc.inventory.models.ProductOutput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOutputRepository extends JpaRepository<ProductOutput, Long> {

    List<ProductOutput> findByProductId(Long id);

}
