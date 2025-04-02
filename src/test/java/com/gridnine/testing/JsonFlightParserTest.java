package com.gridnine.testing;

import com.gridnine.testing.filters.JsonFlightParser;
import com.gridnine.testing.records.Flight;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonFlightParserTest {

    @Test
    void testParseValidFlights() throws IOException {
        List<Flight> flights = JsonFlightParser.parse("src/test/resources/flights.json");

        assertNotNull(flights);
        assertFalse(flights.isEmpty());
        assertEquals(9, flights.size(), "Должно быть 9 полётов");
    }

    @Test
    void testParseInvalidFilePath() {
        assertThrows(IOException.class, () -> JsonFlightParser.parse("invalid/path.json"));
    }

    @Test
    void testParseEmptyOrInvalidJson() {
        assertThrows(FileNotFoundException.class, () -> {
            JsonFlightParser.parse("src/test/resources/empty_or_invalid.json");
        });
    }
}