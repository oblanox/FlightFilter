
package com.gridnine.testing.records;

import java.time.LocalDateTime;
import java.util.Objects;

public record Segment(LocalDateTime departureDate, LocalDateTime arrivalDate) {

    @Override
    public String toString() {
        return "[" + departureDate + " -> " + arrivalDate + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Segment segment = (Segment) o;
        return Objects.equals(arrivalDate, segment.arrivalDate) && Objects.equals(departureDate, segment.departureDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departureDate, arrivalDate);
    }
}
