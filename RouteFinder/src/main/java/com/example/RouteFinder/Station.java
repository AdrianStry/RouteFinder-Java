package com.example.RouteFinder;

public class Station {
    // id,latitude,longitude,name
    int ID;
    float latitude;
    float longitude;
    String stationName;

    public Station(int ID, float latitude, float longitude, String stationName) {
        this.ID = ID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.stationName = stationName;
    }

    public int getID() {
        return ID;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getStationName() {
        return stationName;
    }

    public String toString() {
        return ID + ", " + latitude + ", " + longitude + ", " + stationName + "\n";
    }
}
