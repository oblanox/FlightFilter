package com.gridnine.testing.util;

import com.gridnine.testing.util.parsers.JsonParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTest {

    @Test
    void testParseJsonObject() {
        String json = "{\"key\":\"value\", \"number\":10, \"float\": 15.5}";
        Object parsed = JsonParser.parseJson(json);

        assertInstanceOf(Map.class, parsed);
        Map<?, ?> map = (Map<?, ?>) parsed;
        assertEquals("value", map.get("key"));
        assertEquals(10L, map.get("number"));
        assertEquals(15.5, map.get("float"));
    }

    @Test
    void testParseJsonArray() {
        String json = "[\"one\", \"two\", 3, 4.5]";
        Object parsed = JsonParser.parseJson(json);

        assertInstanceOf(List.class, parsed);
        List<?> list = (List<?>) parsed;
        assertEquals(4, list.size());
        assertEquals("one", list.get(0));
        assertEquals("two", list.get(1));
        assertEquals(3L, list.get(2));
        assertEquals(4.5, list.get(3));
    }

    @Test
    void testParseInvalidJson() {
        String json = "{key:value}"; // без кавычек
        assertThrows(IllegalArgumentException.class, () -> JsonParser.parseJson(json));
    }
}