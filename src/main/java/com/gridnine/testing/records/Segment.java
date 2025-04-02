
package com.gridnine.testing.records;

import java.time.LocalDateTime;

public record Segment(LocalDateTime departureDate, LocalDateTime arrivalDate) {

    @Override
    public String toString() {
        return "[" + departureDate + " -> " + arrivalDate + "]";
    }
}
