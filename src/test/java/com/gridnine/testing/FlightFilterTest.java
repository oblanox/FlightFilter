package com.gridnine.testing;

import com.gridnine.testing.filters.FieldComparisonFilter;
import com.gridnine.testing.filters.FlightFilter;
import com.gridnine.testing.records.Flight;
import com.gridnine.testing.rules.RuleConfig;
import com.gridnine.testing.rules.RuleGroupConfig;
import com.gridnine.testing.rules.RuleSetParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlightFilterTest {

    private final LocalDateTime referenceNow = LocalDateTime.of(2025, 4, 2, 16, 0);

    @Test
    void testFilteringResults() throws IOException {
        List<Flight> allFlights = JsonFlightParser.parse("src/test/resources/flights.json");
        List<RuleGroupConfig> ruleGroups = RuleSetParser.parse("src/test/resources/rules.json");

        assertEquals(9, allFlights.size(), "Initial flight count should be 9.");

        for (RuleGroupConfig group : ruleGroups) {
            List<Flight> filteredFlights = allFlights;

            for (RuleConfig rule : group.getRules()) {
                String field = (String) rule.getParam("field");
                String operator = (String) rule.getParam("operator");
                Object value = rule.getParam("value");

                FlightFilter baseFilter = new FieldComparisonFilter(field, operator, value, referenceNow);
                filteredFlights = rule.isNegate()
                        ? negateFilter(baseFilter, filteredFlights)
                        : baseFilter.filter(filteredFlights);
            }

            System.out.println("\n=== Проверка группы: " + group.getName() + " ===");
            filteredFlights.forEach(System.out::println);

            switch (group.getName()) {
                case "Рейсы в будущем" -> assertEquals(7, filteredFlights.size());
                case "Вылет после завтра +3ч" -> assertEquals(6, filteredFlights.size());
                case "Сегментов не более 2" -> assertEquals(7, filteredFlights.size());
                case "Без коротких пересадок" -> assertEquals(4, filteredFlights.size());
                case "Долгий перелёт (> 4ч)" -> assertEquals(3, filteredFlights.size());
                case "После 2026 года" -> assertEquals(4, filteredFlights.size());
                case "Больше 1 сегмента и вылет завтра" -> assertEquals(3, filteredFlights.size());
                case "Через 5 дней и 30 мин" -> assertEquals(4, filteredFlights.size());
                case "Пересадка ровно 30 минут" -> assertEquals(1, filteredFlights.size());
                default -> throw new IllegalStateException("Неизвестная группа: " + group.getName());
            }
        }
    }

    private List<Flight> negateFilter(FlightFilter filter, List<Flight> flights) {
        List<Flight> baseFiltered = filter.filter(flights);
        return flights.stream()
                .filter(flight -> !baseFiltered.contains(flight))
                .toList();
    }
}
