package com.joblinker.service;

import com.joblinker.domain.Permission;
import com.joblinker.domain.response.ResultPaginationDTO;
import com.joblinker.repository.PermissionRepository;
import com.joblinker.util.error.CustomException;
import com.joblinker.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission fetchById(Long id){
        return permissionRepository.findById(id)
                .orElseThrow(()->new IdInvalidException("Invalid permission"));
    }
    public boolean isPermissionExist(Permission p) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(
                p.getModule(),
                p.getApiPath(),
                p.getMethod());
    }
    public Permission create(Permission p) {
        if(isPermissionExist(p)){
            throw new CustomException("Permission already exists");
        }
        return this.permissionRepository.save(p);
    }
    public void delete(long id){
        Permission permission=this.fetchById(id);
        permission.getRoles().forEach(role->role.getPermissions().remove(permission));

        this.permissionRepository.delete(permission);
    }
    public ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pPermissions = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pPermissions.getTotalPages());
        mt.setTotal(pPermissions.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pPermissions.getContent());
        return rs;
    }

    public Permission update(Long id,Permission p){
        Permission permission = this.fetchById(id);
        // check exist by module, apiPath and method
        if (this.isPermissionExist(p)) {
            // check name
            if (this.isSameName(p)) {
                throw new IdInvalidException("Permission đã tồn tại.");
            }
        }
        permission.setName(p.getName());
        permission.setApiPath(p.getApiPath());
        permission.setMethod(p.getMethod());
        permission.setModule(p.getModule());

        return this.permissionRepository.save(permission);
    }
    public boolean isSameName(Permission p) {
        Permission permissionDB = this.fetchById(p.getId());
        if (permissionDB != null) {
            if (permissionDB.getName().equals(p.getName()))
                return true;
        }
        return false;
    }

}