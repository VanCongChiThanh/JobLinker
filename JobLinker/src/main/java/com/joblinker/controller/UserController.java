package com.joblinker.controller;

import com.joblinker.domain.User;
import com.joblinker.service.UserService;
import com.joblinker.util.error.IdInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if(users.isEmpty())
            return ResponseEntity.noContent().build(); // return 204 No Content if no users are found
        else
            return ResponseEntity.ok(users);
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id)  {
        if(id >=1500){
            throw new IdInvalidException("ID khong lon hon 1500");
        }
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        String hashPassword=this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User newUser=this.userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws IdInvalidException {
        if(id >= 1500){
            throw new IdInvalidException("ID khong lon hon 1500");
        }
        this.userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // return 204 No Content if the user is deleted successfully
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updateUser) {
        User updatedUser = userService.updateUser(id, updateUser);

        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser); // return 200 OK with the updated user
        } else {
            return ResponseEntity.notFound().build(); // return 404 if the user is not found
        }
    }

}