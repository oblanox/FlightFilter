package com.gridnine.testing;

import com.gridnine.testing.records.Flight;
import com.gridnine.testing.rules.RuleConfig;
import com.gridnine.testing.rules.RuleGroupConfig;
import com.gridnine.testing.rules.RuleSetParser;
import com.gridnine.testing.filters.FieldComparisonFilter;
import com.gridnine.testing.filters.FlightFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Flight> allFlights;
        try {
            allFlights = JsonFlightParser.parse("src/main/resources/flights.json");
        } catch (IOException e) {
            System.err.println("Ошибка чтения JSON с перелётами: " + e.getMessage());
            return;
        }

        List<RuleGroupConfig> ruleGroups;
        try {
            ruleGroups = RuleSetParser.parse("src/main/resources/rules.json");
        } catch (IOException e) {
            System.err.println("Ошибка чтения JSON с правилами: " + e.getMessage());
            return;
        }

        LocalDateTime referenceNow = LocalDateTime.now();

        for (RuleGroupConfig group : ruleGroups) {
            List<Flight> filtered = new ArrayList<>(allFlights);

            for (RuleConfig rule : group.getRules()) {
                String field = (String) rule.getParam("field");
                String operator = (String) rule.getParam("operator");
                Object raw = rule.getParam("value");

                if (!(raw instanceof Comparable<?> value)) {
                    System.err.println("Значение параметра не сравнимо: " + raw);
                    continue;
                }

                FlightFilter baseFilter = new FieldComparisonFilter(field, operator, value, referenceNow);

                if (rule.isNegate()) {
                    filtered.removeAll(baseFilter.filter(filtered));
                } else {
                    filtered = baseFilter.filter(filtered);
                }
            }

            System.out.println("\n=== Группа: " + group.getName() + " ===");
            System.out.println(group.getDescription());
            if (filtered.isEmpty()) {
                System.out.println("Нет подходящих рейсов.");
            } else {
                filtered.forEach(System.out::println);
            }
        }
    }
}
