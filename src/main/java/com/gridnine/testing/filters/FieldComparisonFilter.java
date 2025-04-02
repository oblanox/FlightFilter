package com.gridnine.testing.filters;

import com.gridnine.testing.records.Flight;
import com.gridnine.testing.records.Segment;
import com.gridnine.testing.rules.RuleConfig;
import com.gridnine.testing.util.DebugUtils;

import java.time.Duration;
import java.time.LocalDateTime;
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
        if (!str.startsWith("${") || !str.endsWith("}")) return (Comparable<?>) rawValue;

        String expr = str.substring(2, str.length() - 1).trim();
        Pattern pattern = Pattern.compile("(\\w+)\\s*([+-])\\s*(\\w+)\\((\\d+)\\)");
        Matcher matcher = pattern.matcher(expr);

        if (matcher.matches()) {
            String base = matcher.group(1);
            String sign = matcher.group(2);
            String unit = matcher.group(3);
            int amount = Integer.parseInt(matcher.group(4));

            LocalDateTime baseTime = "dateNow".equals(base) ? referenceNow : null;
            if (baseTime == null) throw new IllegalArgumentException("Unsupported base variable: " + base);

            ChronoUnit chronoUnit = parseChronoUnit(unit);
            return "+".equals(sign)
                    ? baseTime.plus(amount, chronoUnit)
                    : baseTime.minus(amount, chronoUnit);
        }

        if ("dateNow".equals(expr)) return referenceNow;

        throw new IllegalArgumentException("Invalid variable expression: " + str);
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
