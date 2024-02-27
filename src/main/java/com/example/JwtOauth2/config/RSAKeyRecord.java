package com.example.JwtOauth2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "jwt")
public record RSAKeyRecord(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
}


/*
* -> them anh xa trong JwtOauth2Application: "@EnableConfigurationProperties"
* vao day de cau hinh cho file "RSAKeyRecord"
*  -> sau do cau hinh them trong application.propertoes nua!
* */

