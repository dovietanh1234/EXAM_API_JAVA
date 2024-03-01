package com.example.JwtOauth2.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "REFRESH_TOKENS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder //  auto tao ra mot mau thiet ke Builder ( design Builder ) ma ko can viet bat ky doan ma nao
public class RefreshTokenEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "REFRESH_TOKEN", nullable = false, length = 10000)
    private String refreshToken;

    @Column(name = "REVOKED")
    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserInfoEntity user;

}

/*
* Builder co the su dung tren ca Class or Method
* */