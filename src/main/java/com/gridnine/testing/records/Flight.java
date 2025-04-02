
package com.gridnine.testing.records;

import java.util.List;

public record Flight(List<Segment> segments) {

    @Override
    public String toString() {
        return "Flight{" + segments + '}';
    }
}
