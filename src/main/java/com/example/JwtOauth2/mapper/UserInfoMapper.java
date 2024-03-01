package com.example.JwtOauth2.mapper;

import com.example.JwtOauth2.DTO.UserRegistrationDto;
import com.example.JwtOauth2.entities.UserInfoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInfoMapper{
    private final PasswordEncoder passwordEncoder;

    public UserInfoEntity convertToEntity(UserRegistrationDto userRegistrationDto){
            UserInfoEntity userInfoEntity = new UserInfoEntity();
            userInfoEntity.setUsername(userRegistrationDto.userName());
            userInfoEntity.setEmailId(userRegistrationDto.userEmail());
            userInfoEntity.setMobileNumber(userRegistrationDto.userMobileNo());
            userInfoEntity.setRoles(userRegistrationDto.userRole());

            userInfoEntity.setPassword( passwordEncoder.encode(userRegistrationDto.userPassword()) );
            return userInfoEntity;
    }

}
