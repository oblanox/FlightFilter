package com.gridnine.testing.rules;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RuleSetConfigTest {

    @Test
    void testRuleSetConfigSetAndGet() {
        RuleSetConfig ruleSetConfig = new RuleSetConfig();

        RuleGroupConfig groupConfig = new RuleGroupConfig();
        groupConfig.setName("Test Group");

        ruleSetConfig.setRuleGroups(List.of(groupConfig));
        ruleSetConfig.setVariables(Map.of("dateNow", "2025-04-02T00:00"));

        assertNotNull(ruleSetConfig.getRuleGroups());
        assertEquals(1, ruleSetConfig.getRuleGroups().size());
        assertEquals("Test Group", ruleSetConfig.getRuleGroups().get(0).getName());

        assertNotNull(ruleSetConfig.getVariables());
        assertEquals("2025-04-02T00:00", ruleSetConfig.getVariables().get("dateNow"));
    }
}