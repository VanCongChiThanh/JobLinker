package com.joblinker.controller;

import com.joblinker.domain.Role;
import com.joblinker.service.RoleService;
import com.joblinker.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // Create a new role
    @PostMapping("/roles")
    @ApiMessage("Create a new role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role role) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }

    // Fetch all roles with optional filtering and pagination
//    @GetMapping("/roles")
//    @ApiMessage("Fetch roles")
//    public ResponseEntity<List<Role>> getRoles(
//            @Filter Specification<Role> spec, Pageable pageable) {
//        return ResponseEntity.ok(this.roleService.getRoles(spec, pageable));
//    }

    // Fetch a role by ID
    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch a role by ID")
    public ResponseEntity<Role> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.roleService.fetchById(id));
    }

    // Update a role
    @PutMapping("/roles/{id}")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> update(@PathVariable("id") Long id, @Valid @RequestBody Role role) {
        return ResponseEntity.ok(this.roleService.update(id, role));
    }

    // Delete a role
    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        this.roleService.delete(id);
        return ResponseEntity.ok().build();
    }
}