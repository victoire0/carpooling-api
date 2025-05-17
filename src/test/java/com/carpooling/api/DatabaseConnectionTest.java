package com.carpooling.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() {
        assertNotNull(dataSource, "DataSource should not be null");
        
        try (Connection connection = dataSource.getConnection()) {
            assertTrue(connection.isValid(1), "Connection should be valid");
            
            // Test a simple query
            String result = jdbcTemplate.queryForObject("SELECT version()", String.class);
            assertNotNull(result, "PostgreSQL version should not be null");
            System.out.println("PostgreSQL Version: " + result);
            
            // Test database name
            String dbName = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
            assertEquals("carpooling_db", dbName, "Database name should be carpooling_db");
            System.out.println("Connected to database: " + dbName);
            
        } catch (SQLException e) {
            fail("Failed to connect to database: " + e.getMessage());
        }
    }
} 