package com.example.cmpt305finalproject.cmpt305;

import java.util.Objects;

public class Neighbourhood {
    private String name;
    private String id;

    public Neighbourhood(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getNeighbourhoodName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Neighbourhood otherNeighbourhood)) {
            return false;
        }
        return Objects.equals(this.id, otherNeighbourhood.id);
    }

    public int hashCode() {
        return Integer.hashCode(Integer.parseInt(id));
    }
}
