package com.guilhermerblc.inventory.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name = "tb_product_input")
public class ProductInput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Product product;

    @Column(length = 20, nullable = false)
    private String barcode;

    @Column(length = 125, nullable = false)
    private String supplier;

    @Column(name = "purchase_value", precision = 10, scale = 2, nullable = false)
    private BigDecimal purchaseValue;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @Column(length = 1024)
    private String observations;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @JsonIgnoreProperties({"authorities", "hibernateLazyInitializer"})
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    private LocalDateTime modified;

}
