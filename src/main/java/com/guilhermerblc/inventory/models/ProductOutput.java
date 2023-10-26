package com.guilhermerblc.inventory.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "tb_product_output")
public class ProductOutput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Product product;

    private String barcode;

    private String buyer;

    @Column(name = "sale_value", precision = 10, scale = 2)
    private BigDecimal saleValue;

    @Column(name = "sale_date", nullable = false)
    private LocalDateTime saleDate;

    private String observations;

    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    private LocalDateTime modified;

}
