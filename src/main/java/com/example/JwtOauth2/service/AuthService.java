package com.example.JwtOauth2.service;

import com.example.JwtOauth2.DTO.AuthResponseDto;
import com.example.JwtOauth2.DTO.TokenType;
import com.example.JwtOauth2.DTO.UserRegistrationDto;
import com.example.JwtOauth2.config.jwtConfig.JwtTokenGenerator;
import com.example.JwtOauth2.entities.RefreshTokenEntity;
import com.example.JwtOauth2.entities.UserInfoEntity;
import com.example.JwtOauth2.mapper.UserInfoMapper;
import com.example.JwtOauth2.repository.IRefreshToken;
import com.example.JwtOauth2.repository.IUserInfoEntity;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final IUserInfoEntity iUserInfoEntity;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final IRefreshToken iRefreshToken;

    private final UserInfoMapper userInfoMapper;

        // SIGN IN ---> ACCESS TOKEN (RESPONSE OBJECT) + REFRESH TOKEN( WITHOUT PERMISSIONS TO ACCESS DATA API) IN HTTP ONLY COOKIE
    public AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response){
        try{

            var userInfoEntity = iUserInfoEntity.findByEmailId(authentication.getName())
                    .orElseThrow( () -> {
                        log.error("[AuthService:userSignInAuth] User :{} not found", authentication.getName());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND");
                    } );

            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

            saveUserRefreshToken(userInfoEntity,refreshToken);
            createRefreshTokenCookie(response, refreshToken);
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
//HttpServletResponse -> đại dện cho một Response HTTP ứng dụng web gửi về cho client
    // add cookie vào response cho client
    private Cookie createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {

        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(15 * 24 * 60 * 60 );
        response.addCookie(refreshTokenCookie);
        return refreshTokenCookie;

    }

    private void saveUserRefreshToken(UserInfoEntity userInfoEntity, String refreshToken) {
        var refreshTokenEntity = RefreshTokenEntity.builder()
                .user(userInfoEntity)
                .refreshToken(refreshToken)
                .revoked(false)
                .build();
        iRefreshToken.save(refreshTokenEntity);
    }


    public Object getAccessTokenUsingRefreshToken(String authorizationHeader) {

        if(   !authorizationHeader.startsWith(TokenType.Bearer.name())   ){
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "please verify your token type");
        }

        final String refreshToken = authorizationHeader.substring(7);

        // find refresh token from db and should not be revoked: same thing can be done through filter

        var refreshTokenEntity = iRefreshToken.findByRefreshToken(refreshToken)
                .filter( tokens -> !tokens.isRevoked() ) // check xem co thoa man dieu kien ko? revoke = false <-> !revoke == true
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Refresh token revoked"));

        UserInfoEntity userInfoEntity = refreshTokenEntity.getUser();

        // now create the authentication object:
        Authentication authentication = createAuthenticationObject(userInfoEntity); // xac thuc va uy quyen nguoi dung.

        // use the authentication Object to generate new accessToken as the Authentication object
        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .accessTokenExpiry(5*60)
                .username(userInfoEntity.getUsername())
                .tokenType(TokenType.Bearer)
                .build();
    }

    private static Authentication createAuthenticationObject(UserInfoEntity userInfoEntity) {
        // Extract user details from UserDetailEntity:
        String username = userInfoEntity.getEmailId();
        String password = userInfoEntity.getPassword();
        String  roles = userInfoEntity.getRoles();

        // extract authorities from roles( comma-separated )
        String[] roleArray = roles.split(","); // trong case client has many roles -> split() se cat cac role do va ngan cach boi dau ","
        GrantedAuthority[] authorities = Arrays.stream(roleArray)
                .map(role -> (GrantedAuthority) role::trim) // moi vai tro se bi cat bo khoang trang va EP KIEP VE "GrantedAuthority"
                .toArray(GrantedAuthority[]::new); // chuyen doi stream thanh mot mang GrantedAuthority[]
        // moi phan tu se new GrantedAuthority và dua vao mang GrantedAuthority[]

        return new UsernamePasswordAuthenticationToken(username, password, Arrays.asList(authorities));
        // UsernamePasswordAuthenticationToken  -> la mot class cua Authentication
        // dung de: bieu dien thong tin xac thuc cho viec dang nhap bang ten nguoi dung và mat khau.
        // No se chua: ten nguoi dung, mat khau, các roles cua người dùng.

        // Tac dung: instance UsernamePasswordAuthenticationToken co the su dung boi Spring security de xac thuc
        // va uy quyen nguoi dung.

    }

    public AuthResponseDto registerUser(UserRegistrationDto userRegistrationDto, HttpServletResponse httpServletResponse) {
            try {
                log.info("[AuthService:registerUser]User Registration Started with :::{}",userRegistrationDto);

                Optional<UserInfoEntity> user = iUserInfoEntity.findByEmailId(userRegistrationDto.userEmail());
                if( user.isPresent() ){
                        throw new Exception("User already exist");
                }

                UserInfoEntity userDetailsEntity = userInfoMapper.convertToEntity(userRegistrationDto);
                // tao authentication cho tai khoan:
    // cho phep Spring Security sử dụng thông tin đã được xác thực khi thực hiện các quyết định về ủy quyền
                Authentication authentication = createAuthenticationObject(userDetailsEntity);

                /*
                * authentication la mot interface trong spring security chua thong tin chi tiet ve nguoi dung
                * da duoc xac thuc ( username, password ) va cac quyen roles cua ho
                * */

                // generate jwt
                String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
                String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

                UserInfoEntity saveUserDetails = iUserInfoEntity.save(userDetailsEntity);

                saveUserRefreshToken(userDetailsEntity, refreshToken);

                createRefreshTokenCookie(httpServletResponse, refreshToken);

                log.info("[AuthService:registerUser] User:{} Successfully registered",saveUserDetails.getUsername());

                return AuthResponseDto.builder()
                        .accessToken(accessToken)
                        .accessTokenExpiry(5*60)
                        .username(saveUserDetails.getUsername())
                        .tokenType(TokenType.Bearer)
                        .build();

            } catch (Exception e) {
                log.error("[AuthService:registerUser]Exception while registering the user due to :"+e.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
            }


    }
}
