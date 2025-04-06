package com.gridnine.testing.records;

import java.util.List;
import java.util.Objects;

public record Filter(
        String name,
        String description,
        Long segmentsCount,
        List<Flight> flights
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filter filter = (Filter) o;
        return Objects.equals(name, filter.name) && Objects.equals(description, filter.description)
                && Objects.equals(segmentsCount, filter.segmentsCount) && Objects.equals(flights, filter.flights);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, segmentsCount, flights);
    }
}
