package com.gridnine.testing.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DebugUtils {

    private static final boolean DEBUG;

    static {
        boolean debugValue = false;
        try (InputStream input = DebugUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                String value = props.getProperty("custom-debug", "false");
                debugValue = Boolean.parseBoolean(value.trim());
            }
        } catch (IOException e) {
            System.err.println("[WARN] Не удалось прочитать application.properties: " + e.getMessage());
        }
        DEBUG = debugValue;
    }

    public static void debug(String message) {
        if (DEBUG) {
            System.out.println("[DEBUG] " + message);
        }
    }

    public static boolean isDebug() {
        return DEBUG;
    }
}
