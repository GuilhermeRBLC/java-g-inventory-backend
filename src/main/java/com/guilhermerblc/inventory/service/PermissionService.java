package com.guilhermerblc.inventory.service;

import com.guilhermerblc.inventory.models.Permission;

import java.util.List;

public interface PermissionService {

    List<Permission> findAll();

    Permission findById(Long id);

    Permission crate(Permission entity);

    Permission update(Long id, Permission entity);

    void delete(Long id);

}
