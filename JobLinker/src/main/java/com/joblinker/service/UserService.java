package com.joblinker.service;

import com.joblinker.domain.User;
import com.joblinker.domain.dto.Meta;
import com.joblinker.domain.dto.ResCreateUserDTO;
import com.joblinker.domain.dto.ResUserDTO;
import com.joblinker.domain.dto.ResultPaginationDTO;
import com.joblinker.repository.GenericSpecification;
import com.joblinker.repository.UserRepository;
import com.joblinker.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        userRepository.save(user);  // save user to the database
        return user;
    }
    public boolean deleteUser(long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);  // delete user from the database
            return true;  // deletion successful
        } else {
            return false;  // user not found
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();  // fetch all users from the database
    }
    public ResultPaginationDTO getUserList(Pageable pageable, GenericSpecification<User> spec){
        Page<User> page = userRepository.findAll(spec,pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        Meta meta=new Meta();
        meta.setPage(page.getNumber() + 1);
        meta.setPageSize(page.getSize());

        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        result.setMeta(meta);

        List<ResUserDTO> listUsers = page.getContent()
                        .stream().map(item->new ResUserDTO(
                                item.getId(),
                                item.getName(),
                                item.getEmail(),
                                item.getAge(),
                                item.getAddress(),
                                item.getGender(),
                                item.getCreatedAt(),
                                item.getUpdatedAt()))
                .collect(Collectors.toList());
        result.setResult(listUsers);
        return result;
    }
    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User with Id: "+userId +" not found"));
    }
    public  User getUserbyEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email: " + email + " not found"));
    }
    public User updateUser(long userId, User updateUser)
    {
        User existingUser=userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User with Id: "+userId +" not found"));// update user in the database
        existingUser.setName(updateUser.getName());
        existingUser.setGender(updateUser.getGender());
        existingUser.setAddress(updateUser.getAddress());
        existingUser.setAge(updateUser.getAge());
        return userRepository.save(existingUser);
    }
    public boolean isEmailExist(String email){
        return userRepository.existsByEmail(email);  // check if email already exists in the database
    }
    public ResCreateUserDTO convertToResCreateUserDTO(User user){
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setName(user.getName());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setAge(user.getAge());
        resCreateUserDTO.setAddress(user.getAddress());
        resCreateUserDTO.setGender(user.getGender());
        resCreateUserDTO.setCreatedAt(user.getCreatedAt());
        return resCreateUserDTO;
    }
    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO resUserDTO = new ResUserDTO();
        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());
        return resUserDTO;
    }
}
