
package com.gridnine.testing.records;

import java.util.List;
import java.util.Objects;

public record Flight(List<Segment> segments) {

    @Override
    public String toString() {
        return "Flight{" + segments + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return Objects.equals(segments, flight.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(segments);
    }
}
