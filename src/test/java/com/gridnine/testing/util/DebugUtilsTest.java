package com.gridnine.testing.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DebugUtilsTest {

    @Test
    void testDebugStateFromProperties() {
        // Предполагается, что в application.properties задан custom-debug=true или false
        boolean debugState = DebugUtils.isDebug();

        assertDoesNotThrow(() -> {
            boolean state = DebugUtils.isDebug();
            System.out.println("Debug state: " + state);
        });
    }

    @Test
    void testDebugOutput() {
        assertDoesNotThrow(() -> DebugUtils.debug("Тестовое сообщение для отладки."));
    }
}
