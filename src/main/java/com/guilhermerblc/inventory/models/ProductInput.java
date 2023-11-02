package com.guilhermerblc.inventory.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name = "tb_product_input")
public class ProductInput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @JsonIgnoreProperties("hibernateLazyInitializer")
    private Product product;

    @Column(length = 20, nullable = false)
    @Size(max = 20)
    private String barcode;

    @Column(length = 125, nullable = false)
    @Size(max = 125)
    private String supplier;

    @Column(name = "purchase_value", precision = 10, scale = 2, nullable = false)
    private BigDecimal purchaseValue;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @Column(nullable = false)
    private Long quantity;

    @Column(length = 1024)
    @Size(max = 1024)
    private String observations;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @JsonIgnoreProperties({"authorities", "hibernateLazyInitializer", "permissions"})
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    private LocalDateTime modified;

}
