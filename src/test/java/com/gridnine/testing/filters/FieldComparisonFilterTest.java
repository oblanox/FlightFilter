package com.gridnine.testing.filters;

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
        List<Flight> result = filter.filter(List.of(flight, flightOld));

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
        List<Flight> result = filter.filter(List.of(flightOneSegment, flightTwoSegments));

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
        List<Flight> result = filter.filter(List.of(flightShortGround, flightLongGround));

        assertEquals(1, result.size());
        assertTrue(result.contains(flightLongGround));
    }

    @Test
    void testUnsupportedField() {
        Flight flight = new Flight(List.of(new Segment(referenceNow, referenceNow.plusHours(1))));
        FlightFilter filter = new FieldComparisonFilter("unsupported", ">", 1);

        assertThrows(IllegalArgumentException.class, () -> filter.filter(List.of(flight)));
    }
}
