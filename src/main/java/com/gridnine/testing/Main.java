package com.gridnine.testing;

import com.gridnine.testing.records.Flight;
import com.gridnine.testing.rules.RuleGroupConfig;
import com.gridnine.testing.rules.RuleConfig;
import com.gridnine.testing.rules.RuleSetParser;
import com.gridnine.testing.filters.FieldComparisonFilter;
import com.gridnine.testing.filters.FlightFilter;
import com.gridnine.testing.util.JsonFlightParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Flight> allFlights;
        try {
            allFlights = JsonFlightParser.parseFlights("src/main/resources/generated_flights.json");
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

                final FlightFilter baseFilter = new FieldComparisonFilter(field, operator, value, referenceNow);

                FlightFilter filter = rule.isNegate()
                        ? flights -> {
                    List<Flight> result = new ArrayList<>(flights);
                    result.removeAll(baseFilter.filter(flights));
                    return result;
                }
                        : baseFilter;

                filtered = filter.filter(filtered);
            }

            System.out.println("\n=== Группа: " + group.getName() + " ===");
            if (filtered.isEmpty()) {
                System.out.println("Нет подходящих рейсов.");
            } else {
                for (Flight f : filtered) {
                    System.out.println(f);
                }
            }
        }
    }
}