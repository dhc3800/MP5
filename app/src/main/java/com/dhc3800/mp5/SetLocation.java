package com.dhc3800.mp5;

public class SetLocation {
    public double Latitude;
    public double Longitude;
    public String address;
    public String id;
    public String name;

    public SetLocation(double latitude, double longitude, String id, String address, String name) {
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.id = id;
        this.address = address;
        this.name = name;
    }
}
