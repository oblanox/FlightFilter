package com.gridnine.testing.rules;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RuleGroupConfigTest {

    @Test
    void testRuleGroupConfigSetAndGet() {
        RuleGroupConfig groupConfig = new RuleGroupConfig();

        groupConfig.setName("Test Group");
        groupConfig.setDescription("A description for test group.");

        RuleConfig ruleConfig = new RuleConfig();
        ruleConfig.setNegate(false);
        ruleConfig.setParams(Map.of("field", "departure", "operator", ">", "value", "2025-04-02T00:00"));

        groupConfig.setRules(List.of(ruleConfig));

        assertEquals("Test Group", groupConfig.getName());
        assertEquals("A description for test group.", groupConfig.getDescription());
        assertNotNull(groupConfig.getRules());
        assertEquals(1, groupConfig.getRules().size());
        assertEquals(ruleConfig, groupConfig.getRules().get(0));
    }
}