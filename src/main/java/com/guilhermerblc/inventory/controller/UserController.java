package com.guilhermerblc.inventory.controller;

import com.guilhermerblc.inventory.models.User;
import com.guilhermerblc.inventory.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService service;

    @GetMapping
    @Secured("VIEW_USERS")
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Secured("VIEW_USERS")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Secured("EDIT_USERS")
    public ResponseEntity<User> create(@RequestBody User body) {
        return ResponseEntity.ok(service.crate(body));
    }

    @PutMapping("/{id}")
    @Secured("EDIT_USERS")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User body) {
        return ResponseEntity.ok(service.update(id, body));
    }

    @DeleteMapping("/{id}")
    @Secured("DELETE_USERS")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
