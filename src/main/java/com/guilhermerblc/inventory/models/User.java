package com.guilhermerblc.inventory.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity(name = "tb_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 125, nullable = false)
    private String name;

    @Column(length = 125, nullable = false)
    private String role;

    @Column(length = 20, nullable = false, unique = true)
    private String username;

    @Column(length = 125, nullable = false)
    private String password;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private Status status = Status.DEACTIVE;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private List<Permission> permissions;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    private LocalDateTime modified;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions.stream().map( a -> new SimpleGrantedAuthority( "ROLE_" + a.getDescription()) ).toList();
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
