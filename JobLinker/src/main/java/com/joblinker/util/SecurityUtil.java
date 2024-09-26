package com.joblinker.util;

import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class SecurityUtil {
    private final JwtEncoder jwtEncoder;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public static final MacAlgorithm JWS_ALGORITHM = MacAlgorithm.HS512;
    @Value("${jwt.base64-secret}")
    private String jwtKey;

    @Value("${jwt_expiration}")
    private Long jwtExpiration;

    private SecretKey getSecretKey(){
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWS_ALGORITHM.getName());
    }
    public String createToken(Authentication authentication) {
        // Lấy thời điểm hiện tại
        Instant now = Instant.now();

        // Xác định thời gian hết hạn (token validity)
        Instant validity = now.plus(jwtExpiration, ChronoUnit.SECONDS);

        // Tạo JwtClaimsSet chứa thông tin của token
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("demoRest") // Đặt tên ứng dụng của bạn làm issuer
                .issuedAt(now)            // Thời điểm phát hành token
                .expiresAt(validity)       // Thời điểm hết hạn của token
                .subject(authentication.getName())  // Tên người dùng (authentication principal)
                .claim("roles", authentication.getAuthorities()) // Thêm thông tin vai trò người dùng
                .build();
        JwsHeader header = JwsHeader.with(JWS_ALGORITHM).build();
        // Mã hóa token và trả về chuỗi JWT
        return this.jwtEncoder.encode(JwtEncoderParameters.from(header,claims)).getTokenValue();
    }
    /**
     * Get the login of the current user.
     *
     * @return the login of the current user
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    Object principal = authentication.getPrincipal();
                    if (principal instanceof UserDetails) {
                        // Nếu principal là kiểu UserDetails, lấy tên người dùng từ UserDetails
                        UserDetails springSecurityUser = (UserDetails) principal;
                        return springSecurityUser.getUsername();
                    } else if (principal instanceof Jwt) {
                        // Nếu principal là kiểu Jwt, lấy tên người dùng từ JWT claims (ví dụ, "sub")
                        Jwt jwt = (Jwt) principal;
                        return jwt.getClaimAsString("sub"); // Lấy claim 'sub' chứa tên người dùng
                    } else if (principal instanceof String) {
                        // Trường hợp principal là một chuỗi (ví dụ: token)
                        return (String) principal;
                    }
                    return null;
                });
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
//    public static boolean isAuthenticated() {
//        SecurityContext securityContext = SecurityContextHolder.getContext();
//        return Optional.ofNullable(securityContext.getAuthentication())
//                .map(authentication -> authentication.getAuthorities().stream()
//                        .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(AuthoritiesConstants.ANONYMOUS)))
//                .orElse(false);
//    }

    /**
     * If the current user has a specific authority (security role).
     * <p>
     * The name of this method comes from the isUserInRole() method in the Servlet API
     *
     * @param authority the authority to check
     * @return true if the current user has the authority, false otherwise
     */
    public static boolean isCurrentUserInRole(String authority) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority)))
                .orElse(false);
    }
}