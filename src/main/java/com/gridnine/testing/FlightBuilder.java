package com.gridnine.testing;

import com.gridnine.testing.filters.FieldComparisonFilter;
import com.gridnine.testing.filters.FlightFilter;
import com.gridnine.testing.filters.JsonFlightParser;
import com.gridnine.testing.records.Filter;
import com.gridnine.testing.records.Flight;
import com.gridnine.testing.rules.RuleConfig;
import com.gridnine.testing.rules.RuleGroupConfig;
import com.gridnine.testing.rules.RuleSetParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class FlightBuilder {

    public static List<Filter> createFlights(String ruleSetJsonPath) {
        List<Flight> allFlights;
        try {
            allFlights = JsonFlightParser.parse("src/main/resources/flights.json");
        } catch (IOException e) {
            System.err.println("Ошибка чтения JSON с перелётами: " + e.getMessage());
            return List.of();
        }

        List<RuleGroupConfig> ruleGroups;
        try {
            ruleGroups = RuleSetParser.parse(ruleSetJsonPath);
        } catch (IOException e) {
            System.err.println("Ошибка чтения JSON с правилами: " + e.getMessage());
            return List.of();
        }

        LocalDateTime referenceNow = LocalDateTime.now();
        List<Filter> result = new ArrayList<>();

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
                    filtered.removeAll(baseFilter.flights(filtered));
                } else {
                    filtered = baseFilter.flights(filtered);
                }
            }

            int totalSegments = filtered.stream()
                    .mapToInt(f -> f.segments().size())
                    .sum();

            result.add(new Filter(
                    group.getName(),
                    group.getDescription(),
                    filtered
            ));
        }

        return result;
    }
}
