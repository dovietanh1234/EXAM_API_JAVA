package com.example.JwtOauth2.repository;

import com.example.JwtOauth2.entities.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserInfoEntity extends JpaRepository<UserInfoEntity, Long> {

    Optional<UserInfoEntity> findByEmailId(String emailId);
}
