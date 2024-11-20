package com.joblinker.controller;

import com.joblinker.domain.Permission;
import com.joblinker.domain.response.ResultPaginationDTO;
import com.joblinker.service.PermissionService;
import com.joblinker.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
    @PostMapping("/permissions")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission permission){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(permission));
    }
    @DeleteMapping("/permissions/{id}")
    @ApiMessage("delete a permission")
    public ResponseEntity<Void> delete(@PathVariable("id") long id)  {
        this.permissionService.delete(id);
        return ResponseEntity.ok().body(null);
    }
    @GetMapping("/permissions")
    @ApiMessage("Fetch permissions")
    public ResponseEntity<ResultPaginationDTO> getPermissions(
            @Filter Specification<Permission> spec, Pageable pageable) {

        return ResponseEntity.ok(this.permissionService.getPermissions(spec, pageable));
    }
    @PutMapping("/permissions/{id}")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> update(@PathVariable("id") Long id, @Valid @RequestBody Permission permission) {
        return ResponseEntity.ok(this.permissionService.update(id, permission));
    }

}