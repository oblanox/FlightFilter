package com.gridnine.testing.rules;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RuleSetParserTest {

    @Test
    void testParseValidFile() throws IOException {
        List<RuleGroupConfig> groups = RuleSetParser.parse("src/test/resources/rules.json");
        assertNotNull(groups);
        assertFalse(groups.isEmpty());

        RuleGroupConfig firstGroup = groups.get(0);
        assertEquals("Рейсы в будущем", firstGroup.getName());
        assertEquals(1, firstGroup.getRules().size());

        RuleConfig firstRule = firstGroup.getRules().get(0);
        assertEquals("arrival", firstRule.getParam("field"));
        assertEquals(">", firstRule.getParam("operator"));
        assertNotNull(firstRule.getParam("value"));
    }

    @Test
    void testParseInvalidFilePath() {
        assertThrows(IOException.class, () -> RuleSetParser.parse("invalid/path.json"));
    }

    @Test
    void testParseEmptyOrInvalidJson() {
        assertThrows(FileNotFoundException.class, () -> {
            RuleSetParser.parse("src/test/resources/empty_or_invalid.json");
        });
    }
}
