package com.joblinker.service;

import com.joblinker.domain.Company;
import com.joblinker.domain.User;
import com.joblinker.domain.response.User.ResCreateUserDTO;
import com.joblinker.domain.response.User.ResUpdateUserDTO;
import com.joblinker.domain.response.User.ResUserDTO;
import com.joblinker.domain.response.ResultPaginationDTO;
import com.joblinker.repository.CompanyRepository;
import com.joblinker.repository.UserRepository;
import com.joblinker.util.error.CustomException;
import com.joblinker.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CompanyRepository companyRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        String hashedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new CustomException("Email already exists");
        }

        // Associate company if provided
        if (user.getCompany() != null) {
            Company company = companyRepository.findById(user.getCompany().getId())
                    .orElse(null);
            user.setCompany(company);
        }

        // Save user and return
        return userRepository.save(user);
    }
    public boolean deleteUser(long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        } else {
            return false;
        }
    }
    public ResultPaginationDTO getAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listUser);

        return rs;
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User with Id: "+userId +" not found"));
    }
    public  User getUserbyEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email: " + email + " not found"));
    }
    public User updateUser(Long id, User updateUser) {
        User existingUser = this.getUserById(id);
        if (existingUser != null) {
            existingUser.setName(updateUser.getName());
            existingUser.setAge(updateUser.getAge());
            existingUser.setAddress(updateUser.getAddress());
            existingUser.setGender(updateUser.getGender());
            if (updateUser.getCompany() != null) {
                Optional<Company> company = this.companyRepository.findById(updateUser.getCompany().getId());
                existingUser.setCompany(company.isPresent() ? company.get() : null);
            }
            existingUser = this.userRepository.save(existingUser);
        }
        return existingUser;
    }
    public ResCreateUserDTO convertToResCreateUserDTO(User user){
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser com=new ResCreateUserDTO.CompanyUser();
        if(user.getCompany()!=null){
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            resCreateUserDTO.setCompany(com);
        }
        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setName(user.getName());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setAge(user.getAge());
        resCreateUserDTO.setAddress(user.getAddress());
        resCreateUserDTO.setGender(user.getGender());
        resCreateUserDTO.setCreatedAt(user.getCreatedAt());
        return resCreateUserDTO;
    }
    public ResUpdateUserDTO convertToResUpdateUserDTO(User user){
        ResUpdateUserDTO resUpdateUserDTO = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser com=new ResUpdateUserDTO.CompanyUser();
        if(user.getCompany()!=null){
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            resUpdateUserDTO.setCompany(com);
        }
        resUpdateUserDTO.setId(user.getId());
        resUpdateUserDTO.setName(user.getName());
        resUpdateUserDTO.setEmail(user.getEmail());
        resUpdateUserDTO.setAge(user.getAge());
        resUpdateUserDTO.setAddress(user.getAddress());
        resUpdateUserDTO.setGender(user.getGender());
        resUpdateUserDTO.setUpdateAt(user.getUpdatedAt());
        return resUpdateUserDTO;
    }
    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser com=new ResUserDTO.CompanyUser();
        if(user.getCompany()!=null){
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);
        }
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;


    }
    public void updateUserToken(String token,String email){
        User currentUser =this.getUserbyEmail(email);
        if(currentUser != null){
            currentUser.setRefreshToken(token);
            userRepository.save(currentUser);
        }
    }
    public User getUserByRefreshTokenAndEmail(String token,String email){
        return userRepository.findByRefreshTokenAndEmail(token,email);
    }
    public boolean checkEmailExists(String email){
        return userRepository.existsByEmail(email);
    }
}