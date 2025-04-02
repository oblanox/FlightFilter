package com.gridnine.testing.filters;

import com.gridnine.testing.exceptions.InvalidDateTimeFormatException;
import com.gridnine.testing.exceptions.UnsupportedFilterVariableException;
import com.gridnine.testing.records.Flight;
import com.gridnine.testing.records.Segment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FieldComparisonFilterTest {

    private final LocalDateTime referenceNow = LocalDateTime.of(2025, 4, 2, 12, 0);

    @Test
    void testFilterByDeparture() {
        Flight flight = new Flight(List.of(new Segment(referenceNow.plusDays(1), referenceNow.plusDays(1).plusHours(2))));
        Flight flightOld = new Flight(List.of(new Segment(referenceNow.minusDays(1), referenceNow.minusHours(23))));

        FlightFilter filter = new FieldComparisonFilter("departure", ">", referenceNow, referenceNow);
        List<Flight> result = filter.flights(List.of(flight, flightOld));

        assertEquals(1, result.size());
        assertTrue(result.contains(flight));
    }

    @Test
    void testFilterBySegmentCount() {
        Flight flightOneSegment = new Flight(List.of(new Segment(referenceNow, referenceNow.plusHours(1))));
        Flight flightTwoSegments = new Flight(List.of(
                new Segment(referenceNow, referenceNow.plusHours(1)),
                new Segment(referenceNow.plusHours(2), referenceNow.plusHours(3))));

        FlightFilter filter = new FieldComparisonFilter("segmentCount", "<=", 1);
        List<Flight> result = filter.flights(List.of(flightOneSegment, flightTwoSegments));

        assertEquals(1, result.size());
        assertTrue(result.contains(flightOneSegment));
    }

    @Test
    void testFilterByGroundTime() {
        Flight flightShortGround = new Flight(List.of(
                new Segment(referenceNow, referenceNow.plusHours(1)),
                new Segment(referenceNow.plusMinutes(80), referenceNow.plusHours(3))));

        Flight flightLongGround = new Flight(List.of(
                new Segment(referenceNow, referenceNow.plusHours(1)),
                new Segment(referenceNow.plusHours(3), referenceNow.plusHours(4))));

        FlightFilter filter = new FieldComparisonFilter("groundTime", ">", 90);
        List<Flight> result = filter.flights(List.of(flightShortGround, flightLongGround));

        assertEquals(1, result.size());
        assertTrue(result.contains(flightLongGround));
    }

    @Test
    void testUnsupportedField() {
        Flight flight = new Flight(List.of(new Segment(referenceNow, referenceNow.plusHours(1))));
        FlightFilter filter = new FieldComparisonFilter("unsupported", ">", 1);

        assertThrows(UnsupportedFilterVariableException.class, () -> filter.flights(List.of(flight)));
    }

    @Test
    void testInvalidDateTimeFormat() {
        String invalidDate = "2025-04-02T12:00:XYZ";

        FlightFilter filter = new FieldComparisonFilter("departure", ">", invalidDate);
        Flight flight = new Flight(List.of(new Segment(referenceNow.plusHours(1), referenceNow.plusHours(2))));

        assertThrows(UnsupportedFilterVariableException.class, () -> filter.flights(List.of(flight)));
    }

    @Test
    void testUnknownFieldName() {
        Flight flight = new Flight(List.of(new Segment(referenceNow, referenceNow.plusHours(1))));
        FlightFilter filter = new FieldComparisonFilter("unknownField", ">", 1);

        assertThrows(UnsupportedFilterVariableException.class, () -> filter.flights(List.of(flight)));
    }

    @Test
    void testUnsupportedChronoUnit() {
        String expr = "${dateNow+lightyear(1)}";

        Flight flight = new Flight(List.of(new Segment(referenceNow.plusDays(2), referenceNow.plusDays(2).plusHours(2))));
        FlightFilter filter = new FieldComparisonFilter("departure", ">", expr, referenceNow);

        assertThrows(UnsupportedFilterVariableException.class, () -> filter.flights(List.of(flight)));
    }

    @Test
    void testUnknownVariableInExpression() {
        String expr = "${unknownVariable}";

        Flight flight = new Flight(List.of(new Segment(referenceNow.plusDays(2), referenceNow.plusDays(2).plusHours(2))));
        FlightFilter filter = new FieldComparisonFilter("departure", ">", expr, referenceNow);

        assertThrows(UnsupportedFilterVariableException.class, () -> filter.flights(List.of(flight)));
    }

    @Test
    void testUnsupportedParameterFormat() {
        String expr = "abc123";

        Flight flight = new Flight(List.of(new Segment(referenceNow.plusDays(2), referenceNow.plusDays(2).plusHours(2))));
        FlightFilter filter = new FieldComparisonFilter("departure", ">", expr, referenceNow);

        assertThrows(UnsupportedFilterVariableException.class, () -> filter.flights(List.of(flight)));
    }

    @Test
    void testInvalidIsoDate_ThrowsCustomException() {
        String malformed = "2025-04-02T25:99";

        Flight flight = new Flight(List.of(new Segment(referenceNow, referenceNow.plusHours(2))));
        FlightFilter filter = new FieldComparisonFilter("departure", ">", malformed, referenceNow);

        assertThrows(InvalidDateTimeFormatException.class, () -> filter.flights(List.of(flight)));
    }
}
