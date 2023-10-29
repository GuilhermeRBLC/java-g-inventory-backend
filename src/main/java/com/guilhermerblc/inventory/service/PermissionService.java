package com.guilhermerblc.inventory.service;

import com.guilhermerblc.inventory.models.Permission;

import java.util.List;

public interface PermissionService {

    List<Permission> findAll();

    Permission findById(Long id);

}
