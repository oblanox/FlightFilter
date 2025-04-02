package com.gridnine.testing.filters;

import com.gridnine.testing.records.Flight;
import com.gridnine.testing.records.Segment;
import com.gridnine.testing.util.parsers.JsonParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonFlightParser {

    public static List<Flight> parse(String filePath) throws IOException {
        String json = JsonParser.readFileAsString(filePath);
        Object parsed = JsonParser.parseJson(json);

        List<Flight> flights = new ArrayList<>();

        if (parsed instanceof List<?> flightList) {

            for (Object flightObj : flightList) {
                if (!(flightObj instanceof Map<?, ?> flightMap)) continue;

                Object segmentsObj = flightMap.get("segments");
                if (!(segmentsObj instanceof List<?> segmentsList)) continue;

                List<Segment> segments = new ArrayList<>();
                for (Object segmentObj : segmentsList) {
                    if (!(segmentObj instanceof Map<?, ?> segmentMap)) continue;

                    Object departureRaw = segmentMap.get("departure");
                    Object arrivalRaw = segmentMap.get("arrival");

                    if (departureRaw instanceof String depStr && arrivalRaw instanceof String arrStr) {
                        LocalDateTime departure = LocalDateTime.parse(depStr);
                        LocalDateTime arrival = LocalDateTime.parse(arrStr);
                        segments.add(new Segment(departure, arrival));
                    }
                }

                if (!segments.isEmpty()) {
                    flights.add(new Flight(segments));
                }
            }
        }

        return flights;
    }
}
