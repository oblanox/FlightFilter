package com.gridnine.testing.util.parsers;

import com.gridnine.testing.exceptions.JsonParsingException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class JsonParser {

    public static Object parseJson(String json) {
        json = json.trim();
        if (json.startsWith("{")) {
            return parseJsonObject(json);
        } else if (json.startsWith("[")) {
            return parseJsonArray(json);
        } else if (json.startsWith("\"") && json.endsWith("\"")) {
            return json.substring(1, json.length() - 1);
        } else if (json.equalsIgnoreCase("true") || json.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(json);
        } else if (json.matches("-?\\d+\\.\\d+")) {
            return Double.parseDouble(json);
        } else if (json.matches("-?\\d+")) {
            return Long.parseLong(json);
        } else {
            throw new JsonParsingException("Invalid JSON value: " + json);
        }
    }

    public static Map<String, Object> parseJsonObject(String json) {
        Map<String, Object> map = new LinkedHashMap<>();
        json = json.substring(1, json.length() - 1).trim();
        String[] entries = splitJsonEntries(json);
        for (String entry : entries) {
            String[] keyValue = entry.split(":", 2);
            if (keyValue.length != 2) continue;
            String key = parseJson(keyValue[0]).toString();
            Object value = parseJson(keyValue[1]);
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> parseJsonArray(String json) {
        List<Object> list = new ArrayList<>();
        json = json.substring(1, json.length() - 1).trim();
        String[] values = splitJsonEntries(json);
        for (String value : values) {
            list.add(parseJson(value));
        }
        return list;
    }

    private static String[] splitJsonEntries(String json) {
        List<String> entries = new ArrayList<>();
        int braceCount = 0;
        int bracketCount = 0;
        boolean inQuotes = false;
        StringBuilder entry = new StringBuilder();
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"') inQuotes = !inQuotes;
            if (!inQuotes) {
                if (c == '{') braceCount++;
                else if (c == '}') braceCount--;
                else if (c == '[') bracketCount++;
                else if (c == ']') bracketCount--;
                else if (c == ',' && braceCount == 0 && bracketCount == 0) {
                    entries.add(entry.toString().trim());
                    entry.setLength(0);
                    continue;
                }
            }
            entry.append(c);
        }
        if (!entry.isEmpty()) {
            entries.add(entry.toString().trim());
        }
        return entries.toArray(new String[0]);
    }

    public static String readFileAsString(String path) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }
}
