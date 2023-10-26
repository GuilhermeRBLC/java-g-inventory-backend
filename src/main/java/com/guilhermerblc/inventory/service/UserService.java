package com.guilhermerblc.inventory.service;


import com.guilhermerblc.inventory.models.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long id);

    User crate(User entity);

    User update(Long id, User entity);

    void delete(Long id);

}
