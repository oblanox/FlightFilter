package com.gridnine.testing;

import com.gridnine.testing.records.Filter;
import com.gridnine.testing.records.Flight;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.setProperty("skipInvalidSegments","false"); // параметр защиты от ошибок, нужно выключить по условию задачи
        List<Filter> results = FlightBuilder.createFlights("src/main/resources/rules.json");

        for (Filter filter : results) {
            System.out.printf("\n=== %s ===\n%s\nПерелётов: %d\n",
                    filter.name(),
                    filter.description(),
                    filter.flights().size());

            for (Flight flight : filter.flights()) {
                System.out.println("  " + flight);
            }
        }
    }
}
