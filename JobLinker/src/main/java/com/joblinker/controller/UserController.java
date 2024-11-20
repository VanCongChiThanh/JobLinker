package com.joblinker.controller;

import com.joblinker.domain.User;
import com.joblinker.domain.response.User.ResCreateUserDTO;
import com.joblinker.domain.response.User.ResUpdateUserDTO;
import com.joblinker.domain.response.ResultPaginationDTO;
import com.joblinker.service.UserService;
import com.joblinker.util.annotation.ApiMessage;
import com.joblinker.util.error.CustomException;
import com.joblinker.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
//    @GetMapping("/users")
//    @ApiMessage("fetch users")
//    public ResponseEntity<ResultPaginationDTO> getAllUsers(
//            Pageable pageable,
//            @RequestParam(value = "key", required = false) Optional<String> keyOptional,
//            @RequestParam(value = "operation", required = false) Optional<String> operationOptional,
//            @RequestParam(value = "value", required = false) Optional<String> valueOptional
//    ) {
//        SearchCriteria searchCriteria = buildSearchCriteria(keyOptional, operationOptional, valueOptional);
//        GenericSpecification<User> spec=new GenericSpecification<>(searchCriteria);
//        ResultPaginationDTO users = userService.getUserList(pageable, spec);
//        return ResponseEntity.ok(users);
//    }
//    private SearchCriteria buildSearchCriteria(Optional<String> key, Optional<String> operation, Optional<String> value) {
//        if (key.isPresent() && operation.isPresent() && value.isPresent()) {
//            return new SearchCriteria(key.get(), operation.get(), value.get());
//        }
//        return null;
//    }

    @GetMapping("/users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
            @Filter Specification<User> specification,
            Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.userService.getAllUser(specification, pageable));
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id)  throws IdInvalidException{
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) throws CustomException {
        boolean isEmailExist=this.userService.checkEmailExists(user.getEmail());
        if(isEmailExist){
            throw new CustomException("Email "+user.getEmail()+" da ton tai");
        }

        String hashPassword=this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User newUser=this.userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        this.userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // return 204 No Content if the user is deleted successfully
    }


    @PutMapping("/users/{id}")
    public ResponseEntity<ResUpdateUserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody User updateUser) {
        User updatedUser = userService.updateUser(id, updateUser);
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(updatedUser));
    }

}