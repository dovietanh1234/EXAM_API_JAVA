package com.example.JwtOauth2.config.userConfig;

import com.example.JwtOauth2.entities.UserInfoEntity;
import com.example.JwtOauth2.repository.IUserInfoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

//@RequiredArgsConstructor
//@Component
//@Slf4j
//public class InitialUserInfo implements CommandLineRunner {
//
//    @Autowired
//    private final IUserInfoEntity iUserInfoEntity;
//
//    // cau hinh passwordEmcoder  trong SecurityConfig
//    @Autowired
//    private final PasswordEncoder passwordEncoder;
//
//
//
//    @Override
//    public void run(String... args) throws Exception {
//        UserInfoEntity manager = new UserInfoEntity();
//        manager.setUsername("Manager");
//        manager.setPassword(passwordEncoder.encode("password"));
//        manager.setRoles("ROLE_MANAGER");
//        manager.setEmailId("manager@manager.com");
//
//        UserInfoEntity admin = new UserInfoEntity();
//        admin.setUsername("Admin");
//        admin.setPassword(passwordEncoder.encode("password"));
//        admin.setRoles("ROLE_ADMIN");
//        admin.setEmailId("admin@admin.com");
//
//        UserInfoEntity user = new UserInfoEntity();
//        user.setUsername("User");
//        user.setPassword(passwordEncoder.encode("password"));
//        user.setRoles("ROLE_USER");
//        user.setEmailId("user@user.com");
//
//        iUserInfoEntity.saveAll(List.of(manager, admin, user));
//
//    }
//}
/*
*  @Slf4j -> auto create an instance logger tinh cho class InitialUserInfo
* No cho phep ta ghi lai thong tin ve hoat dong cua ung dung.
* Logger co the su dung de ghi lai cac thong diep voi nhieu muc do khac nhau: DEBUG, INFO, WARN, ERROR...
*
*
*
*
*
* */