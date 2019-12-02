package com.foxminded.rodin.courses.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class ApplicationTest {

    @Test
    public void shouldInitiateDatabase() {
        assertDoesNotThrow(() -> {
            Application.initiateDatabase();
        });
    }

    @Test
    public void shouldGenerateData() {
        assertDoesNotThrow(() -> {
            Application.generateData();
        });
    }

}
