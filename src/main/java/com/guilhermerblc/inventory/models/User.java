package com.guilhermerblc.inventory.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity(name = "tb_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 125, nullable = false)
    private String name;

    @Column(length = 125, nullable = false)
    private String role;

    @Column(length = 20, nullable = false)
    private String username;

    @Column(length = 125, nullable = false)
    private String password;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private Status status = Status.DEACTIVE;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Permission> permissions;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    private LocalDateTime modified;

}
