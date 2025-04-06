package com.gridnine.testing.records;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SegmentTest {

    @Test
    void testSegmentCreation() {
        LocalDateTime departure = LocalDateTime.of(2025, 4, 2, 14, 0);
        LocalDateTime arrival = departure.plusHours(2);

        Segment segment = new Segment(departure, arrival);

        assertEquals(departure, segment.departureDate());
        assertEquals(arrival, segment.arrivalDate());
    }

    @Test
    void testSegmentToString() {
        LocalDateTime departure = LocalDateTime.of(2025, 4, 2, 14, 0);
        LocalDateTime arrival = departure.plusHours(2);

        Segment segment = new Segment(departure, arrival);

        String expectedString = "[2025-04-02T14:00 -> 2025-04-02T16:00]";
        assertEquals(expectedString, segment.toString());
    }
}
