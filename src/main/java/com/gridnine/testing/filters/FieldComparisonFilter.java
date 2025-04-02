package com.gridnine.testing.filters;

import com.gridnine.testing.exceptions.InvalidDateTimeFormatException;
import com.gridnine.testing.exceptions.UnsupportedFilterVariableException;
import com.gridnine.testing.records.Flight;
import com.gridnine.testing.records.Segment;
import com.gridnine.testing.rules.RuleConfig;
import com.gridnine.testing.util.DebugUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FieldComparisonFilter implements FlightFilter {

    private static final String FIELD_DEPARTURE = "departure";
    private static final String FIELD_ARRIVAL = "arrival";
    private static final String FIELD_SEGMENT_COUNT = "segmentCount";
    private static final String FIELD_GROUND_TIME = "groundTime";
    private static final String FIELD_TOTAL_DURATION = "totalFlightDuration";
    private static final String VAR_DATE_NOW = "dateNow";

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
        this.value = value;
    }

    public List<Flight> flights(List<Flight> flights) {
        return flights.stream()
                .filter(this::isValid)
                .filter(this::matches)
                .peek(f -> DebugUtils.debug(String.format(
                        "Flight matched filter: field=%s operator=%s value=%s => %s",
                        field, operator, value, f)))
                .collect(Collectors.toList());
    }

    private boolean isValid(Flight flight) {
        String prop = System.getProperty("skipInvalidSegments", "true");
        boolean skip = Boolean.parseBoolean(prop);

        if (!skip) return true;

        for (Segment segment : flight.segments()) {
            if (segment.arrivalDate().isBefore(segment.departureDate())) {
                DebugUtils.debug("Skipped invalid segment: " + segment);
                return false;
            }
        }
        return true;
    }

    private boolean matches(Flight flight) {
        Map<String, Comparable<?>> context = buildFlightContext(flight);
        Comparable<?> comparisonValue = resolveComparisonValue(this.value, context);

        if (FIELD_DEPARTURE.equals(field) || FIELD_ARRIVAL.equals(field)) {
            return RuleConfig.evaluate(operator,
                    (LocalDateTime) context.get(field),
                    (LocalDateTime) comparisonValue);
        } else if (FIELD_SEGMENT_COUNT.equals(field) || FIELD_GROUND_TIME.equals(field) || FIELD_TOTAL_DURATION.equals(field)) {
            return RuleConfig.evaluate(operator,
                    toLong(context.get(field)),
                    toLong(comparisonValue));
        }

        throw new UnsupportedFilterVariableException("Unsupported field: " + field);
    }

    private Map<String, Comparable<?>> buildFlightContext(Flight flight) {
        Map<String, Comparable<?>> context = new HashMap<>();
        List<Segment> segments = flight.segments();

        context.put(FIELD_DEPARTURE, segments.get(0).departureDate());
        context.put(FIELD_ARRIVAL, segments.get(segments.size() - 1).arrivalDate());
        context.put(FIELD_SEGMENT_COUNT, (long) segments.size());

        long groundTime = 0L;
        for (int i = 1; i < segments.size(); i++) {
            groundTime += Duration.between(
                    segments.get(i - 1).arrivalDate(),
                    segments.get(i).departureDate()).toMinutes();
        }
        context.put(FIELD_GROUND_TIME, groundTime);

        long totalDuration = Duration.between(
                segments.get(0).departureDate(),
                segments.get(segments.size() - 1).arrivalDate()).toMinutes();
        context.put(FIELD_TOTAL_DURATION, totalDuration);

        return context;
    }

    private Comparable<?> resolveComparisonValue(Object raw, Map<String, Comparable<?>> context) {
        if (!(raw instanceof String str)) return (Comparable<?>) raw;
        str = str.trim();

        if (str.startsWith("${") && str.endsWith("}")) {
            String expr = str.substring(2, str.length() - 1).trim();
            if (expr.startsWith(VAR_DATE_NOW)) {
                return evaluateDateArithmetic(referenceNow, expr.substring(VAR_DATE_NOW.length()));
            }

            if (context.containsKey(expr)) {
                return context.get(expr);
            }

            throw new UnsupportedFilterVariableException("Неизвестная переменная: " + expr);
        }

        return resolveVariable(raw);
    }

    private Comparable<?> resolveVariable(Object rawValue) {
        if (!(rawValue instanceof String str)) return (Comparable<?>) rawValue;

        str = str.trim();

        if (str.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?")) {
            try {
                return LocalDateTime.parse(str.length() == 16 ? str + ":00" : str);
            } catch (DateTimeParseException e) {
                throw new InvalidDateTimeFormatException("Invalid ISO datetime format: " + str, e);
            }
        }

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

        if (str.matches("-?\\d+")) return Long.parseLong(str);
        if (str.matches("-?\\d+\\.\\d+")) return Double.parseDouble(str);

        throw new UnsupportedFilterVariableException("Unsupported parameter format: " + str);
    }

    private LocalDateTime evaluateDateArithmetic(LocalDateTime base, String expression) {
        String expr = expression.trim();
        Matcher opMatcher = Pattern.compile("([+-])\\s*(\\w+)\\((\\d+)\\)").matcher(expr);
        LocalDateTime result = base;
        while (opMatcher.find()) {
            String op = opMatcher.group(1);
            String unit = opMatcher.group(2);
            int value = Integer.parseInt(opMatcher.group(3));
            ChronoUnit chronoUnit = parseChronoUnit(unit);

            if ("+".equals(op)) {
                result = result.plus(value, chronoUnit);
            } else {
                result = result.minus(value, chronoUnit);
            }
        }
        return result;
    }

    private ChronoUnit parseChronoUnit(String unit) {
        return switch (unit.toLowerCase()) {
            case "sec" -> ChronoUnit.SECONDS;
            case "min" -> ChronoUnit.MINUTES;
            case "hour" -> ChronoUnit.HOURS;
            case "day" -> ChronoUnit.DAYS;
            case "week" -> ChronoUnit.WEEKS;
            case "month" -> ChronoUnit.MONTHS;
            default -> throw new UnsupportedFilterVariableException("Unsupported time unit: " + unit);
        };
    }

    private Long toLong(Object val) {
        if (val instanceof Long l) return l;
        if (val instanceof Integer i) return i.longValue();
        if (val instanceof Double d) return d.longValue();
        return Long.parseLong(val.toString());
    }
}
