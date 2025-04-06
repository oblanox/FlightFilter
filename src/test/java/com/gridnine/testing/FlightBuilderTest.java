package com.gridnine.testing;

import com.gridnine.testing.records.Filter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlightBuilderTest {

    @Test
    void testCreateFlights() {
        System.setProperty("skipInvalidSegments", "false"); // не скипаем, нужен битый рейс

        List<Filter> filters = FlightBuilder.createFlights("src/test/resources/rules.json");

        assertFalse(filters.isEmpty(), "Filters should not be empty");

        Filter filter = filters.get(10);
        assertEquals("Сегменты с датой прилёта раньше даты вылета", filter.name());
        assertEquals("Исключает рейсы, где хотя бы один сегмент имеет прилёт раньше вылета", filter.description());
        assertFalse(filter.flights().isEmpty());
    }
}
