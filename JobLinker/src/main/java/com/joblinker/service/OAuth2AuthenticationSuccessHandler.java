package com.joblinker.service;

import com.joblinker.domain.User;
import com.joblinker.domain.response.ResLoginDTO;
import com.joblinker.util.SecurityUtil;
import com.joblinker.util.constant.Provider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final RoleService roleService;
    public final PasswordEncoder passwordEncoder;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(UserService userService, SecurityUtil securityUtil, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.securityUtil = securityUtil;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        if (!this.userService.checkEmailExists(email)) {
            User newUser=new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setProvider(Provider.GOOGLE);
            newUser.setRole(roleService.fetchById(3L));
            newUser.setPassword(UUID.randomUUID().toString());
            this.userService.createUser(newUser);
        }
        User user = userService.getUserbyEmail(email);
        ResLoginDTO res = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
        res.setUser(userLogin);

        String access_token = securityUtil.createAccessToken(email, res);
        String refresh_token = securityUtil.createRefreshToken(email, res);
        res.setAccessToken(access_token);

        // Cập nhật refresh token trong database
        userService.updateUserToken(refresh_token, email);

        // Cấu hình cookie chứa refresh token (chỉ gửi qua HTTP)
        ResponseCookie resCookie = ResponseCookie
                .from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .maxAge(7 * 24 * 60 * 60)
                .path("/")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, resCookie.toString());

        String redirectUrl = "http://localhost:8081/oauth2/callback?token=" + access_token;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}