package com.example.JwtOauth2;

import com.example.JwtOauth2.config.RSAKeyRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RSAKeyRecord.class)
@SpringBootApplication
public class JwtOauth2Application {

	public static void main(String[] args) {
		SpringApplication.run(JwtOauth2Application.class, args);
	}

}
/*
*  -> them anh xa: "@EnableConfigurationProperties" vao day de cau hinh cho file "RSAKeyRecord"
*  -> sau do cau hinh them trong application.propertoes nua!
*
*
*
* */