package com.example.cmpt305finalproject.cmpt305;

import java.util.Objects;

public class Address {
    private String suite;
    private String houseNumber;
    private String streetName;

    public Address(String suite, String houseNumber, String streetName) {
        this.suite = suite;
        this.houseNumber = houseNumber;
        this.streetName = streetName;
    }

    public String toString() {
        return this.suite + " " + this.houseNumber + " " + this.streetName;
    }

    public int hashCode() {
        return Objects.hash(this.suite, this.houseNumber, this.streetName);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Address otherAddress)) {
            return false;
        }
        return obj.toString().equals(this.toString());
    }
}
