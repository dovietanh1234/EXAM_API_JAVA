package com.example.JwtOauth2.service;

import com.example.JwtOauth2.DTO.AuthResponseDto;
import com.example.JwtOauth2.DTO.TokenType;
import com.example.JwtOauth2.config.jwtConfig.JwtTokenGenerator;
import com.example.JwtOauth2.repository.IUserInfoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final IUserInfoEntity iUserInfoEntity;
    private final JwtTokenGenerator jwtTokenGenerator;
    public AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication){
        try{

            var userInfoEntity = iUserInfoEntity.findByEmailId(authentication.getName())
                    .orElseThrow( () -> {
                        log.error("[AuthService:userSignInAuth] User :{} not found", authentication.getName());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND");
                    } );

            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

            log.info("[AuthService:userSignInAuth] Access token for user: {}, has been generated", userInfoEntity.getEmailId());

            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(15*60)
                    .username(userInfoEntity.getUsername())
                    .tokenType(TokenType.Bearer)
                    .build();
            // day kieu giong nhu tao mot doi tuong:
            // AuthResponseDto a = new AuthResponseDto();
            // a.setAccessToken(accessToken); ...


        }catch (Exception e){
            log.error("[AuthService:userSignInAuth] Access token is error");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
