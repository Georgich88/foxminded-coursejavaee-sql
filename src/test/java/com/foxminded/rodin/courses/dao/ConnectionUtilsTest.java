package com.foxminded.rodin.courses.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

import com.foxminded.rodin.courses.dao.exceptions.NoDBPropertiesException;

public class ConnectionUtilsTest {

    @Test
    public void shouldEstablishConnection() {
        assertDoesNotThrow(() -> {
            Connection connection = ConnectionUtils.getConnection();
            connection.close();
        });
    }

    @Test
    public void shouldThrowNoDBPropertiesExceptionWhenGetsWrongPropertiesFilePath() {
        assertThrows(NoDBPropertiesException.class, () -> {
            Connection connection = ConnectionUtils.getConnection("src/wrong-main/resources/db.properties");
            connection.close();
        });
    }
    
}
