package com.gridnine.testing.rules;

import java.util.Map;

public class RuleConfig {
    private boolean negate;
    private Map<String, Object> params;

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Object getParam(String key) {
        return params != null ? params.get(key) : null;
    }

    public static <T extends Comparable<T>> boolean evaluate(String operator, T actual, T expected) {
        if (actual == null || expected == null || operator == null) return false;

        return switch (operator) {
            case ">" -> actual.compareTo(expected) > 0;
            case ">=" -> actual.compareTo(expected) >= 0;
            case "<" -> actual.compareTo(expected) < 0;
            case "<=" -> actual.compareTo(expected) <= 0;
            case "==" -> actual.compareTo(expected) == 0;
            case "!=" -> actual.compareTo(expected) != 0;
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }
}
