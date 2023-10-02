package com.example.RouteFinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StationTest {

    private Station station;

    @BeforeEach
    public void setUp() {
        station = new Station(1, 35.6895f, 139.6917f, "Tokyo Station");
    }

    @Test
    public void testGetID() {
        assertEquals(1, station.getID(), "Expected ID to be 1");
    }

    @Test
    public void testGetLatitude() {
        assertEquals(35.6895f, station.getLatitude(), "Expected latitude to be 35.6895");
    }

    @Test
    public void testGetLongitude() {
        assertEquals(139.6917f, station.getLongitude(), "Expected longitude to be 139.6917");
    }

    @Test
    public void testGetStationName() {
        assertEquals("Tokyo Station", station.getStationName(), "Expected station name to be Tokyo Station");
    }

    @Test
    public void testToString() {
        String expectedString = "1, 35.6895, 139.6917, Tokyo Station\n";
        assertEquals(expectedString, station.toString(), "Expected string representation to match");
    }
}