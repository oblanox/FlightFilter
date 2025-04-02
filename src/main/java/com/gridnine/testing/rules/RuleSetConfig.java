package com.gridnine.testing.rules;

import java.util.List;
import java.util.Map;

public class RuleSetConfig {
    private List<RuleGroupConfig> ruleGroups;
    private Map<String, String> variables;

    public List<RuleGroupConfig> getRuleGroups() {
        return ruleGroups;
    }

    public void setRuleGroups(List<RuleGroupConfig> ruleGroups) {
        this.ruleGroups = ruleGroups;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }
}