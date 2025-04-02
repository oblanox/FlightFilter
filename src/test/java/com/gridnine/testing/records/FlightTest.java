package com.gridnine.testing.records;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FlightTest {

    @Test
    void testFlightCreation() {
        Segment segment1 = new Segment(LocalDateTime.of(2025, 4, 2, 14, 0), LocalDateTime.of(2025, 4, 2, 16, 0));
        Segment segment2 = new Segment(LocalDateTime.of(2025, 4, 2, 17, 0), LocalDateTime.of(2025, 4, 2, 18, 30));

        Flight flight = new Flight(List.of(segment1, segment2));

        assertEquals(2, flight.segments().size());
        assertTrue(flight.segments().contains(segment1));
        assertTrue(flight.segments().contains(segment2));
    }

    @Test
    void testFlightToString() {
        Segment segment = new Segment(LocalDateTime.of(2025, 4, 2, 14, 0), LocalDateTime.of(2025, 4, 2, 16, 0));
        Flight flight = new Flight(List.of(segment));

        String expectedString = "Flight{" + flight.segments() + '}';
        assertEquals(expectedString, flight.toString());
    }
}
