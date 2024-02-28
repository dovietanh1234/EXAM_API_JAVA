package com.example.JwtOauth2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "jwt") // chi dinh rang cac thuoc tinh cau hinh voi tien to "jwt" trong file cau hinh "application.properties" se lien ket voi record nay
public record RSAKeyRecord(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
    // khai bao mot record( la 1 class dac biet chi chua du lieu )
}


/*
* -> them anh xa trong JwtOauth2Application: "@EnableConfigurationProperties"
* vao day de cau hinh cho file "RSAKeyRecord"
*  -> sau do cau hinh them trong application.propertoes nua!
* */

