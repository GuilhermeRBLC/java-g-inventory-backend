package com.guilhermerblc.inventory.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tb_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 125, nullable = false)
    @Size(max = 125)
    private String description;

    @Column(length = 50, nullable = false)
    @Size(max = 50)
    private String type;

    @Column(name = "inventory_minimum", nullable = false)
    @Min(value = 1, message = "Should be minimum 1")
    @Max(value = Integer.MAX_VALUE, message = "Number too large.")
    private int inventoryMinimum;

    @Column(name = "inventory_maximum", nullable = false)
    @Min(value = 1, message = "Should be minimum 1")
    @Max(value = Integer.MAX_VALUE, message = "Number too large.")
    private int inventoryMaximum;

    @Column(length = 1024)
    @Size(max = 1024)
    private String observations;

    @JoinColumn(nullable = false)
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"authorities", "hibernateLazyInitializer", "permissions"})
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    private LocalDateTime modified;

}
