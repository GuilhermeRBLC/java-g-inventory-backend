package com.guilhermerblc.inventory.service.impl;

import com.guilhermerblc.inventory.models.User;
import com.guilhermerblc.inventory.repository.UserRepository;
import com.guilhermerblc.inventory.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDetailsService userDetailsService() {
        return username -> repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    public User crate(User entity) {
        return repository.save(entity);
    }

    @Override
    public User update(Long id, User entity) {
        User user = findById(id);

        user.setName(entity.getName());
        user.setRole(entity.getRole());
        user.setPermissions(entity.getPermissions());
        user.setUsername(entity.getUsername());
        user.setPassword(entity.getPassword());
        user.setModified(LocalDateTime.now());

        return repository.save(user);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

}
