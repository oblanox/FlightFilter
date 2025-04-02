package com.gridnine.testing.rules;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RuleConfigTest {

    @Test
    void testEvaluate() {
        assertTrue(RuleConfig.evaluate(">", 5, 3));
        assertFalse(RuleConfig.evaluate(">", 3, 5));

        assertTrue(RuleConfig.evaluate("<", 3, 5));
        assertFalse(RuleConfig.evaluate("<", 5, 3));

        assertTrue(RuleConfig.evaluate(">=", 5, 5));
        assertFalse(RuleConfig.evaluate(">=", 4, 5));

        assertTrue(RuleConfig.evaluate("<=", 4, 5));
        assertFalse(RuleConfig.evaluate("<=", 6, 5));

        assertTrue(RuleConfig.evaluate("==", 5, 5));
        assertFalse(RuleConfig.evaluate("==", 4, 5));

        assertTrue(RuleConfig.evaluate("!=", 4, 5));
        assertFalse(RuleConfig.evaluate("!=", 5, 5));
    }

    @Test
    void testEvaluateWithNulls() {
        assertFalse(RuleConfig.evaluate(">", null, 5));
        assertFalse(RuleConfig.evaluate("<", 5, null));
        assertFalse(RuleConfig.evaluate(null, 5, 5));
    }

    @Test
    void testEvaluateWithInvalidOperator() {
        assertThrows(IllegalArgumentException.class,
                () -> RuleConfig.evaluate("unsupported", 5, 5));
    }
}
