package com.example.JwtOauth2.config.jwtConfig;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenGenerator {

    private final JwtEncoder jwtEncoder; // cau hinh trong file SecurityConfig

    public String generateAccessToken(Authentication authentication){
        log.info("Token creation started for: {}", authentication.getName());

        String roles = getRolesOfUser(authentication);

        String permission = getPermissionFromRoles(roles);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("vietanh") // nguoi tao token
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(1, ChronoUnit.MINUTES))
                .subject(authentication.getName()) // ten subject cua token
                .claim("scope", permission) //  cac quyen co the lam viec trong role
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    }

    public String generateRefreshToken(Authentication authentication){
        log.info("[JwtTokenGenerator:generateRefreshToken] Token Creation Started for:{}", authentication.getName());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("vietanh") // nguoi tao token
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(15, ChronoUnit.DAYS))
                .subject(authentication.getName()) // ten subject cua token
                .claim("scope", "REFRESH_TOKEN") //  cac quyen co the lam viec trong role
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    }

    private static String getRolesOfUser(Authentication authentication){
        return authentication.getAuthorities().stream()// tao ra 1 luong stream lam viec voi mang or string ...
                .map(GrantedAuthority::getAuthority) // lap qua cac phan tu va chuyen doi cac doi tuong GrantedAuthority thanh chuoi quyen tuong ung cua no!
                .collect(Collectors.joining(" "));//ket hop cac chuoi quyen trong stream thanh 1 chuoi duy nhat ( tach nhau bang mot khoang trang )
    }  //GrantedAuthority::getAuthority == GrantedAuthority -> GrantedAuthority.getAuthority();
       // -> ex: ["Hello", "world"] --> .collect(Collectors.joining(" ")) --> "Hello world" String!
    private String getPermissionFromRoles(String roles){
        Set<String> permissions = new HashSet<>();

        if ( roles.contains("ROLE_ADMIN") ){
                permissions.addAll( List.of("READ", "WRITE", "DELETE") );
        }

        if ( roles.contains("ROLE_MANAGER") ){
            permissions.add("READ");
        }

        if(roles.contains("ROLE_USER")){
            permissions.add("READ");
        }

        return String.join(" ", permissions);

    }


}
