package com.joblinker.service;

import com.joblinker.domain.User;
import com.joblinker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with Id: "+userId +"not found"));
    }
    public  User getUserbyEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email: " + email + " not found"));
    }
    public User updateUser(long userId, User updateUser)
    {
        User existingUser=userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with Id: "+userId +"not found"));// update user in the database
        existingUser.setName(updateUser.getName());
        existingUser.setEmail(updateUser.getEmail());
        existingUser.setPassword(updateUser.getPassword());

        return userRepository.save(existingUser);
    }
}
