package com.gridnine.testing.filters;

import com.gridnine.testing.records.Flight;
import com.gridnine.testing.records.Segment;
import com.gridnine.testing.rules.RuleConfig;
import com.gridnine.testing.util.DebugUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FieldComparisonFilter implements FlightFilter {

    private final String field;
    private final String operator;
    private final Object value;
    private final LocalDateTime referenceNow;

    public FieldComparisonFilter(String field, String operator, Object value) {
        this(field, operator, value, LocalDateTime.now());
    }

    public FieldComparisonFilter(String field, String operator, Object value, LocalDateTime referenceNow) {
        this.field = field;
        this.operator = operator;
        this.referenceNow = referenceNow;
        this.value = resolveVariable(value);
    }

    public List<Flight> filter(List<Flight> flights) {
        return flights.stream()
                .filter(this::isValid)
                .filter(this::matches)
                .peek(f -> DebugUtils.debug(String.format(
                        "Flight matched filter: field=%s operator=%s value=%s => %s",
                        field, operator, value, f)))
                .collect(Collectors.toList());
    }
    private boolean isValid(Flight flight) {
        for (Segment segment : flight.segments()) {
            if (segment.arrivalDate().isBefore(segment.departureDate())) {
                DebugUtils.debug("Skipped invalid segment: " + segment);
                return false;
            }
        }
        return true;
    }

    private boolean matches(Flight flight) {
        return switch (field) {
            case "departure" -> RuleConfig.evaluate(operator,
                    flight.segments().get(0).departureDate(),
                    (LocalDateTime) value);

            case "arrival" -> {
                List<Segment> segments = flight.segments();
                yield RuleConfig.evaluate(operator,
                        segments.get(segments.size() - 1).arrivalDate(),
                        (LocalDateTime) value);
            }

            case "segmentCount" -> RuleConfig.evaluate(operator,
                    (long) flight.segments().size(),
                    toLong(value));

            case "groundTime" -> {
                List<Segment> segs = flight.segments();
                long groundTimeMinutes = 0L;
                for (int i = 1; i < segs.size(); i++) {
                    LocalDateTime prevArrival = segs.get(i - 1).arrivalDate();
                    LocalDateTime nextDeparture = segs.get(i).departureDate();
                    groundTimeMinutes += Duration.between(prevArrival, nextDeparture).toMinutes();
                }
                yield RuleConfig.evaluate(operator, groundTimeMinutes, toLong(value));
            }

            case "totalFlightDuration" -> {
                LocalDateTime start = flight.segments().get(0).departureDate();
                LocalDateTime end = flight.segments().get(flight.segments().size() - 1).arrivalDate();
                long totalMinutes = Duration.between(start, end).toMinutes();
                yield RuleConfig.evaluate(operator, totalMinutes, toLong(value));
            }

            default -> throw new IllegalArgumentException("Unsupported field: " + field);
        };
    }

    private Comparable<?> resolveVariable(Object rawValue) {
        if (!(rawValue instanceof String str)) return (Comparable<?>) rawValue;

        str = str.trim();

        // Переменные вида ${...}
        if (str.startsWith("${") && str.endsWith("}")) {
            String expr = str.substring(2, str.length() - 1).trim();

            LocalDateTime result = "dateNow".equals(expr) ? referenceNow : null;

            if (expr.startsWith("dateNow")) {
                result = referenceNow;
                expr = expr.substring(7).trim(); // remove "dateNow"
            }

            // Поддержка нескольких арифметических операций
            Pattern opPattern = Pattern.compile("([+-])\\s*(\\w+)\\((\\d+)\\)");
            Matcher matcher = opPattern.matcher(expr);
            while (matcher.find()) {
                String op = matcher.group(1);
                String unit = matcher.group(2);
                int value = Integer.parseInt(matcher.group(3));

                ChronoUnit chronoUnit = parseChronoUnit(unit);

                if ("+".equals(op)) {
                    assert result != null;
                    result = result.plus(value, chronoUnit);
                } else {
                    assert result != null;
                    result = result.minus(value, chronoUnit);
                }
            }

            if (result != null) return result;
            throw new IllegalArgumentException("Invalid variable expression: " + str);
        }

        // Даты и времена
        if (str.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?")) {
            try {
                return LocalDateTime.parse(str.length() == 16 ? str + ":00" : str);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid ISO datetime format: " + str, e);
            }
        }

        // Простой плюсоминус : "5 + 10"
        if (str.matches("-?\\d+(\\s*[+-]\\s*\\d+)+")) {
            String[] tokens = str.split("\\s*[+-]\\s*");
            String[] ops = str.replaceAll("[^+-]", "").split("");
            int sum = Integer.parseInt(tokens[0].trim());
            for (int i = 1; i < tokens.length; i++) {
                int num = Integer.parseInt(tokens[i].trim());
                sum += ops[i - 1].equals("-") ? -num : num;
            }
            return sum;
        }

        // Простое число
        if (str.matches("-?\\d+")) return Long.parseLong(str);
        if (str.matches("-?\\d+\\.\\d+")) return Double.parseDouble(str);

        throw new IllegalArgumentException("Unsupported parameter format: " + str);
    }



    private ChronoUnit parseChronoUnit(String unit) {
        return switch (unit.toLowerCase()) {
            case "sec" -> ChronoUnit.SECONDS;
            case "min" -> ChronoUnit.MINUTES;
            case "hour" -> ChronoUnit.HOURS;
            case "day" -> ChronoUnit.DAYS;
            case "week" -> ChronoUnit.WEEKS;
            case "month" -> ChronoUnit.MONTHS;
            default -> throw new IllegalArgumentException("Unsupported time unit: " + unit);
        };
    }

    private Long toLong(Object val) {
        if (val instanceof Long l) return l;
        if (val instanceof Integer i) return i.longValue();
        if (val instanceof Double d) return d.longValue();
        return Long.parseLong(val.toString());
    }
}
