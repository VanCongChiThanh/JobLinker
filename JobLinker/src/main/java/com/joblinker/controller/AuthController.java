package com.joblinker.controller;

import com.joblinker.domain.User;
import com.joblinker.domain.request.LoginDTO;
import com.joblinker.domain.response.ResLoginDTO;
import com.joblinker.domain.response.User.ResCreateUserDTO;
import com.joblinker.service.UserService;
import com.joblinker.util.SecurityUtil;
import com.joblinker.util.annotation.ApiMessage;
import com.joblinker.util.error.CustomException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public  AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        //nạp input gồm username và password vào Sercurity
        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        //xác thực người dùng  =>cần viết func  loadUsersbyUsername
        Authentication authentication=authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // Create a token
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res=new ResLoginDTO();
        User currentUserDB=this.userService.getUserbyEmail(loginDTO.getUsername());
        if(currentUserDB !=null){
            ResLoginDTO.UserLogin userLogin=new ResLoginDTO.UserLogin(currentUserDB.getId(),
                                                                      currentUserDB.getEmail(),
                                                                      currentUserDB.getName());
            res.setUser(userLogin);
        }
        String access_token=this.securityUtil.createAccessToken(authentication.getName(), res);
        res.setAccessToken(access_token);

       String refresh_token=this.securityUtil.createRefreshToken(loginDTO.getUsername(),res);
       this.userService.updateUserToken(refresh_token,loginDTO.getUsername());

       ResponseCookie resCookie=ResponseCookie
               .from("refresh_token", refresh_token)
               .httpOnly(true)
               .secure(true)
               .maxAge(refreshTokenExpiration)
               .path("/")
               .build();

       return ResponseEntity.ok()
               .header(HttpHeaders.SET_COOKIE,resCookie.toString())
               .body(res);
    }
    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(value = "refresh_token",defaultValue = "default-cookie") String refresh_token
    ) {
        if("default-cookie".equals(refresh_token))  {
            throw new CustomException("refresh token is empty");
        }
        //check validity of refresh token
        Jwt decodedToken=this.securityUtil.checkValidRefreshToken(refresh_token);
        String email=decodedToken.getSubject();
        User currentUser=this.userService.getUserByRefreshTokenAndEmail(refresh_token,email);
        if(currentUser==null){
            throw new CustomException( "Invalid refresh token");
        }
        ResLoginDTO res=new ResLoginDTO();
        User currentUserDB=this.userService.getUserbyEmail(email);
        if(currentUserDB !=null){
            ResLoginDTO.UserLogin userLogin=new ResLoginDTO.UserLogin(currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName());
            res.setUser(userLogin);
        }
        String access_token=this.securityUtil.createAccessToken(email, res);
        res.setAccessToken(access_token);

        String new_refresh_token=this.securityUtil.createRefreshToken(email,res);
        this.userService.updateUserToken(refresh_token,email);

        ResponseCookie resCookie=ResponseCookie
                .from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .maxAge(refreshTokenExpiration)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,resCookie.toString())
                .body(res);
    }
    @PostMapping("/auth/logout")
    @ApiMessage("Log out")
    public ResponseEntity<Void> logout() throws CustomException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            System.out.println("No authentication found.");
        } else {
            System.out.println("Authentication found: " + authentication.getPrincipal());
        }

        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email.equals("")) {
            throw new CustomException("Access Token invalid");
        }

        this.userService.updateUserToken(null, email);
        ResponseCookie deleteCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)                // Chỉ có thể truy cập từ server
                .secure(true)                  // Chỉ gửi cookie qua HTTPS
                .path("/")                     // Cookie có hiệu lực cho tất cả các đường dẫn
                .maxAge(0)                     // Xóa cookie ngay lập tức
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(null);
    }
    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUserDB = this.userService.getUserbyEmail(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());

            userGetAccount.setUser(userLogin);
        }

        return ResponseEntity.ok().body(userGetAccount);
    }
    @PostMapping()
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User user){


        // Create and save user
        User newUser = userService.createUser(user);

        // Convert to DTO and return response
        ResCreateUserDTO responseDTO = userService.convertToResCreateUserDTO(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

}