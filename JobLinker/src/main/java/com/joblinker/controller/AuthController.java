package com.joblinker.controller;

import com.joblinker.domain.User;
import com.joblinker.domain.dto.LoginDTO;
import com.joblinker.domain.dto.ResLoginDTO;
import com.joblinker.service.UserService;
import com.joblinker.util.SecurityUtil;
import com.joblinker.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    public  AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("auth/login")
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
                                                                      currentUserDB.getName(),
                                                                      currentUserDB.getAddress());
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
               .maxAge(60*60*24*7)
               .build();

       return ResponseEntity.ok()
               .header(HttpHeaders.SET_COOKIE,resCookie.toString())
               .body(res);
    }
    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<Void> getRefreshToken() {
        return ResponseEntity.ok().body(null);
    }
}