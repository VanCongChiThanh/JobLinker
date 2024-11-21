package com.joblinker.service;

import com.joblinker.domain.Permission;
import com.joblinker.domain.Role;
import com.joblinker.repository.PermissionRepository;
import com.joblinker.repository.RoleRepository;
import com.joblinker.util.error.CustomException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }
    public Role fetchById(Long id) {
        return roleRepository.findById(id)
               .orElseThrow(()->new CustomException("Invalid role"));
    }
    public Role create(Role role){
        //check existing role
        if(this.roleRepository.existsByName(role.getName())){
            throw new CustomException("Role with name " + role.getName() + " already exists");
        }
        //check permissions
        if(role.getPermissions() !=null){
            List<Long> reqPermissions = role.getPermissions().stream()
                    .map(p->p.getId()).collect(Collectors.toList());
            List<Permission> dbPermissions=this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(dbPermissions);
        }
        return this.roleRepository.save(role);
    }

    public Role update(Long id, Role role){
        Role existingRole = this.fetchById(id);
        // check existing role
        if(!this.roleRepository.existsByName(role.getName())){
            throw new CustomException("Role with name " + role.getName() + " does not exist");
        }
        //check permissions
        if(role.getPermissions()!=null){
            List<Long> reqPermissions = role.getPermissions().stream()
                   .map(p->p.getId()).collect(Collectors.toList());
            List<Permission> dbPermissions=this.permissionRepository.findByIdIn(reqPermissions);
            existingRole.setPermissions(dbPermissions);
        }
        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());
        existingRole.setActive(role.isActive());
        return this.roleRepository.save(existingRole);
    }
    public void delete(Long id) {
        Role existingRole = this.fetchById(id);
        if (existingRole.getPermissions() != null) {
            existingRole.getPermissions().clear();
            this.roleRepository.save(existingRole);
        }
        this.roleRepository.deleteById(id);
    }

}