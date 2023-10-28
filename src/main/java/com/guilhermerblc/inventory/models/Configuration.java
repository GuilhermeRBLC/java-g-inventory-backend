package com.guilhermerblc.inventory.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "tb_configuration")
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20, nullable = false)
    private String alias;

    @Column(length = 1024, nullable = false)
    private String stored;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    private LocalDateTime modified;

}
