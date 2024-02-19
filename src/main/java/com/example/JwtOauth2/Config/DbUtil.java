package com.example.JwtOauth2.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {
    private static Connection connection = null;

    public static Connection getConnection() {

        if (connection != null) {
            return connection;
        } else {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/springboot4?useSSL=false";
            String user = "root";
            String password = "123456";

            try {

                Class.forName(driver);
                connection = DriverManager.getConnection(url, user, password);

            } catch (ClassNotFoundException | SQLException ex) {
                throw new RuntimeException(ex.getMessage());
            }

        }

        return connection;


    }

}
