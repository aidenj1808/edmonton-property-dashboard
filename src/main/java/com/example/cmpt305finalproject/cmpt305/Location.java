package com.example.cmpt305finalproject.cmpt305;

import java.util.Objects;

public class Location {
    private double latitude;
    private double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String toString() {
        return "(" + this.latitude + ", " + this.longitude + ")";
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Location otherLocation)) {
            return false;
        }
        return obj.toString().equals(this.toString());
    }

    public int hashCode() {return Objects.hash(this.latitude, this.longitude);}
}
