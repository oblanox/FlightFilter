package com.gridnine.testing.rules;

import com.gridnine.testing.util.parsers.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RuleSetParser {

    public static List<RuleGroupConfig> parse(String filePath) throws IOException {
        String jsonContent = JsonParser.readFileAsString(filePath);
        Object parsed = JsonParser.parseJson(jsonContent);

        List<RuleGroupConfig> groups = new ArrayList<>();

        if (parsed instanceof List<?> groupList) {

            for (Object groupObj : groupList) {
                if (!(groupObj instanceof Map<?, ?> groupMap)) continue;

                RuleGroupConfig group = new RuleGroupConfig();
                group.setName((String) groupMap.get("name"));
                group.setDescription((String) groupMap.getOrDefault("description", null));

                List<RuleConfig> rules = new ArrayList<>();
                Object rulesObj = groupMap.get("rules");

                if (rulesObj instanceof List<?> ruleList) {

                    for (Object ruleObj : ruleList) {
                        if (!(ruleObj instanceof Map<?, ?> ruleMap)) continue;

                        RuleConfig rule = new RuleConfig();
                        rule.setNegate(Boolean.TRUE.equals(ruleMap.get("negate")));

                        Object paramsObj = ruleMap.get("params");
                        if (paramsObj instanceof Map<?, ?> paramsMap) {
                            boolean validParams = paramsMap.keySet().stream().allMatch(String.class::isInstance);

                            if (validParams) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> params = (Map<String, Object>) paramsMap;
                                rule.setParams(params);
                                rules.add(rule);
                            }
                        }
                    }
                }

                group.setRules(rules);
                groups.add(group);
            }
        }

        return groups;
    }
}
