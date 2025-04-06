package com.gridnine.testing.rules;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleSetConfig that = (RuleSetConfig) o;
        return Objects.equals(getRuleGroups(), that.getRuleGroups()) && Objects.equals(getVariables(), that.getVariables());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRuleGroups(), getVariables());
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }
}