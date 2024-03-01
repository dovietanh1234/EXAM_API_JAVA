package com.example.JwtOauth2.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
//la mot annotation tong hop @Getter & @Setter ...@ToString, @AllArgsConstructor,@EqualAndHashCode no sinh ra all methods cho thuoc tinh class
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_info")
public class UserInfoEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(nullable = false, name = "email_id", unique = true)
    private String emailId;

    @Column(nullable = false, name = "password")
    private String password;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(nullable = false, name = "ROLES")
    private String roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //@JsonIgnore
    private List<RefreshTokenEntity> refreshTokens;

}

/*
* mappedBy = "user" -> map vào field của RefreshTokenEntity
* cascade = CascadeType.ALL -> chi dinh hoat dong CSDL nao? se ap dung len cac lien ket khi
* thuc the chu so huu duoc luu, cap nhat, xoa CascadeType.ALL( all hoat dong CSDL se ap dung )
*
* fetch = FetchType.LAZY -> du lieu ko duoc tai cho den khi ta truy cap no
* nghia la khi ta truy cap vao User no se ko goi. nhung ta truy cap vao field "refreshTokens"
* cua User thi luc nao no moi goi.
* */
