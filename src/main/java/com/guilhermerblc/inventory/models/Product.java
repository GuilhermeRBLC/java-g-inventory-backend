package com.guilhermerblc.inventory.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "tb_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 125, nullable = false)
    private String description;

    @Column(length = 50, nullable = false)
    private String type;

    @Column(name = "inventory_minimum", nullable = false)
    private int inventoryMinimum;

    @Column(name = "inventory_maximum", nullable = false)
    private int inventoryMaximum;

    @Column(length = 1024)
    private String observations;

    @JoinColumn(nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    private LocalDateTime modified;

}
