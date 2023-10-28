package com.guilhermerblc.inventory.service.impl;

import com.guilhermerblc.inventory.models.Permission;
import com.guilhermerblc.inventory.repository.PermissionRepository;
import com.guilhermerblc.inventory.service.PermissionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository repository;

    @Override
    public List<Permission> findAll() {
        return repository.findAll();
    }

    @Override
    public Permission findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    public Permission crate(Permission entity) {
        entity.setCreated(LocalDateTime.now());
        entity.setModified(null);
        return repository.save(entity);
    }

    @Override
    public Permission update(Long id, Permission entity) {
        Permission permission = findById(id);

        permission.setDescription(entity.getDescription());
        permission.setModified(LocalDateTime.now());

        return repository.save(permission);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
