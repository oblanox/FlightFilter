package com.gridnine.testing.rules;

import java.util.List;
import java.util.Objects;

public class RuleGroupConfig {
    private String name;
    private String description;
    private List<RuleConfig> rules;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RuleConfig> getRules() {
        return rules;
    }

    public void setRules(List<RuleConfig> rules) {
        this.rules = rules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleGroupConfig that = (RuleGroupConfig) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription())
                && Objects.equals(getRules(), that.getRules());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getRules());
    }
}
