package com.example.cmpt305finalproject.cmpt305;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssessValueYearMap extends HashMap<String, Long> {
    private Map<String, Long> map = new HashMap<>();

    public AssessValueYearMap(List<String> data) {
        for (String row : data) {
            String[] entry = row.split(",");
            String year = entry[0];
            Long value = Long.parseLong(entry[1]);
            this.map.put(year, value);
        }
    }

    public Map<String, Long> getAssessValueYearMap() {
        return this.map;
    }
}
