package com.example.JwtOauth2.config.jwtConfig;

import com.example.JwtOauth2.config.userConfig.UserInfoConfig;
import com.example.JwtOauth2.repository.IUserInfoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    public String getUserName(Jwt jwtToken){
        return jwtToken.getSubject();
    }

    public boolean isTokenValid(Jwt jwtToken, UserDetails userDetails){
        final String username = getUserName(jwtToken);
        boolean isTokenExpired = getIfTokenIsExpired(jwtToken);

        boolean isTokenUserSameAsDatabase = username.equals(userDetails.getUsername());
        return !isTokenExpired && isTokenUserSameAsDatabase;
    }

    private boolean getIfTokenIsExpired( Jwt jwtToken ){
        return Objects.requireNonNull(jwtToken.getExpiresAt()).isBefore(Instant.now());
    }


    private final IUserInfoEntity userInfoEntity; // add constructor la oke

    public UserDetails userDetails(String emailId){
        return userInfoEntity
                .findByEmailId(emailId)
                .map(UserInfoConfig::new)
                .orElseThrow( () -> new UsernameNotFoundException("UserEmail: " + emailId + "does not exist"));
    }
//userDetail la 1 interface cung cap cac thong tin co ban ve nguoi dung trong he thong bao mat
// no chua ten, password, roles ...

// UserInfoConfig mot class trien khai tu userDetails giup ta tao ra doi tuong userDetail


}
