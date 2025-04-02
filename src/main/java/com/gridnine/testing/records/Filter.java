package com.gridnine.testing.records;

import java.util.List;

public record Filter(
        String name,
        String description,
        List<Flight> flights
) {
}
