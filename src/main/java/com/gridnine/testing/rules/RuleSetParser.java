package com.gridnine.testing.rules;

import com.gridnine.testing.util.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RuleSetParser {

    public static List<RuleGroupConfig> parse(String filePath) throws IOException {
        String jsonContent = JsonParser.readFileAsString(filePath);
        Object parsed = JsonParser.parseJson(jsonContent);

        List<RuleGroupConfig> groups = new ArrayList<>();

        if (parsed instanceof List) {
            List<?> groupList = (List<?>) parsed;

            for (Object groupObj : groupList) {
                if (!(groupObj instanceof Map)) continue;
                Map<String, Object> groupMap = (Map<String, Object>) groupObj;

                RuleGroupConfig group = new RuleGroupConfig();
                group.setName((String) groupMap.get("name"));
                group.setDescription((String) groupMap.getOrDefault("description", null));

                List<RuleConfig> rules = new ArrayList<>();
                Object rulesObj = groupMap.get("rules");

                if (rulesObj instanceof List) {
                    List<?> ruleList = (List<?>) rulesObj;

                    for (Object ruleObj : ruleList) {
                        if (!(ruleObj instanceof Map)) continue;
                        Map<String, Object> ruleMap = (Map<String, Object>) ruleObj;

                        RuleConfig rule = new RuleConfig();
                        rule.setNegate(Boolean.TRUE.equals(ruleMap.get("negate")));

                        Object paramsObj = ruleMap.get("params");
                        if (paramsObj instanceof Map) {
                            rule.setParams((Map<String, Object>) paramsObj);
                            rules.add(rule);
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
