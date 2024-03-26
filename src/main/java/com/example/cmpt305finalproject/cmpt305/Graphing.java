package com.example.cmpt305finalproject.cmpt305;


import java.util.HashMap;
import java.util.List;

public class Graphing {
    private List<String> info;
    private HashMap<String, Long> yearAccessedValueMap = new HashMap<>();

    public Graphing(List<String> info) {
        this.info = info;

        for (String yearAndValue : this.info) {
            String[] yearValue = yearAndValue.split(",");
            String year = yearValue[0];
            Long value = Long.parseLong(yearValue[1]);

            if (this.yearAccessedValueMap.containsKey(year)) {
                this.yearAccessedValueMap.merge(year, value, Long::sum);
            } else {
                this.yearAccessedValueMap.put(year, value);
            }
        }
    }
    public HashMap<String, Long> getYearAccessedValueMap() {
        return this.yearAccessedValueMap;
    }
    public void printYearAccessedValueMap() {
        System.out.println(this.yearAccessedValueMap);
    }
}
