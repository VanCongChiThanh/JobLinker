package com.joblinker.service;

import com.joblinker.domain.User;
import com.joblinker.domain.dto.ResCreateUserDTO;
import com.joblinker.domain.dto.ResUserDTO;
import com.joblinker.domain.dto.ResultPaginationDTO;
import com.joblinker.repository.GenericSpecification;
import com.joblinker.repository.UserRepository;
import com.joblinker.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    public User updateUser(User updateUser)
    {
        Optional<User> userOptional=this.userRepository.findById(updateUser.getId());
        if(userOptional.isPresent()){
            User existingUser = userOptional.get();
            existingUser.setEmail(updateUser.getEmail());
            existingUser.setAge(updateUser.getAge());
            existingUser.setAddress(updateUser.getAddress());
            existingUser.setGender(updateUser.getGender());
            existingUser.setUpdatedBy(updateUser.getUpdatedBy());
            return this.userRepository.save(existingUser);
        }
        return null;
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
