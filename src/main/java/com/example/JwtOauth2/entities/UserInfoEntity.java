package com.example.JwtOauth2.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

}

