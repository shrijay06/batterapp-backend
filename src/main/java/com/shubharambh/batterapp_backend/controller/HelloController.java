package com.shubharambh.batterapp_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@RestController
public class HelloController {
    private final DataSource dataSource;

    @Autowired
    public HelloController(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @GetMapping("/api/hello")
    public String sayHello() {
        String result = "DB not connected";
        String sql = "SELECT 'DB-connected' AS db_res";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                result = rs.getString("db_res");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = "DB error: " + e.getMessage();
        }

        return "Hello from Batter App Backend! [" + result + "]";
    }
}