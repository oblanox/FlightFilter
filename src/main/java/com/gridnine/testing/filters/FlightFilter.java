package com.gridnine.testing.filters;

import com.gridnine.testing.records.Flight;
import java.util.List;

public interface FlightFilter {
    List<Flight> flights(List<Flight> flights);
}
