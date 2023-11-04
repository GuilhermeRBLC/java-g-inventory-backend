package com.guilhermerblc.inventory.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"authorities"})
@Entity(name = "tb_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 125, nullable = false)
    @Size(max = 125)
    private String name;

    @Column(length = 125, nullable = false)
    @Size(max = 125)
    private String role;

    @Column(length = 20, nullable = false, unique = true)
    @Size(max = 20)
    private String username;

    // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    // TODO: Está ocorrendo um erro por conta de não deixar a senha ser retornada na api,
    // diz que o rawPassword não pode ser null, vou deiar comentado até que volte a internet e eu possa pesquisar.
    @Column(length = 125, nullable = false)
    @Size(max = 125)
    private String password;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private Status status = Status.DEACTIVE;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private List<Permission> permissions;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    private LocalDateTime modified;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions.stream().map( a -> new SimpleGrantedAuthority( a.getDescription()) ).toList();
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
