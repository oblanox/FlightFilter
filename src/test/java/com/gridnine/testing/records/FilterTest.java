package com.gridnine.testing.records;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterTest {

    private final LocalDateTime now = LocalDateTime.of(2025, 4, 2, 12, 0);

    @Test
    void testEqualsAndHashCode() {
        Flight flight1 = new Flight(List.of(new Segment(now, now.plusHours(2))));
        Flight flight2 = new Flight(List.of(new Segment(now, now.plusHours(2)))); // same content

        Filter filter1 = new Filter("TestFilter", "Description", 1L, List.of(flight1));
        Filter filter2 = new Filter("TestFilter", "Description", 1L, List.of(flight2));

        assertEquals(filter1, filter2, "Filters should be equal");
        assertEquals(filter1.hashCode(), filter2.hashCode(), "Hash codes should match");
    }

    @Test
    void testNotEquals() {
        Flight flight1 = new Flight(List.of(new Segment(now, now.plusHours(2))));
        Flight flight2 = new Flight(List.of(new Segment(now.plusDays(1), now.plusDays(1).plusHours(2))));

        Filter filter1 = new Filter("FilterA", "SameDesc", 1L, List.of(flight1));
        Filter filter2 = new Filter("FilterA", "SameDesc", 1L, List.of(flight2));

        assertNotEquals(filter1, filter2, "Filters should not be equal due to different flights");
    }

    @Test
    void testEqualsWithItselfAndNull() {
        Flight flight = new Flight(List.of(new Segment(now, now.plusHours(2))));
        Filter filter = new Filter("Self", "Desc", 1L, List.of(flight));

        assertEquals(filter, filter, "Filter must equal itself");
        assertNotEquals(filter, null, "Filter must not equal null");
    }

    @Test
    void testEqualsWithDifferentClass() {
        Flight flight = new Flight(List.of(new Segment(now, now.plusHours(2))));
        Filter filter = new Filter("X", "Y", 1L, List.of(flight));

        assertNotEquals(filter, "not a filter");
    }
}
