package com.example.cmpt305finalproject.cmpt305;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class DataAnalysis {
    private PropertyAssessments propertyAssessments;
    private List<Neighbourhood> neighbourhoods = new ArrayList<>();

    private List<Integer> allNeighbourhoodValues = new ArrayList<>();
    private HashMap<String, Integer> allNeighbourhoodsAndValues = new HashMap<>();
    private LinkedHashMap<String, Integer> sortedAllNeighbourhoodAndValues = new LinkedHashMap<>();

    private List<Integer> residentialNeighbourhoodValues = new ArrayList<>();
    private HashMap<String, Integer> residentialNeighbourhoodsAndValues = new HashMap<>();
    private LinkedHashMap<String, Integer> sortedResidentialNeighbourhoodAndValues = new LinkedHashMap<>();

    public DataAnalysis(PropertyAssessments propertyAssessments) {
        this.propertyAssessments = propertyAssessments;

        for (int i = 0; i < this.propertyAssessments.getSize(); i++) {
            Neighbourhood neighbourhood = this.propertyAssessments.get(i).getNeighbourhood();
            if (!neighbourhoods.contains(neighbourhood)) {
                neighbourhoods.add(neighbourhood);
                allNeighbourhoodValues.add(0);
                residentialNeighbourhoodValues.add(0);
            }
            int allIndex = neighbourhoods.indexOf(neighbourhood);
            Integer allValue = allNeighbourhoodValues.get(allIndex);
            allValue += this.propertyAssessments.get(i).getAssessedValue();
            allNeighbourhoodValues.set(allIndex, allValue);

            if (this.propertyAssessments.get(i).getAssessmentClasses().onlyContains("Residential")) {
                int residentialIndex = neighbourhoods.indexOf(neighbourhood);
                Integer residentialValue = residentialNeighbourhoodValues.get(residentialIndex);
                residentialValue += this.propertyAssessments.get(i).getAssessedValue();
                residentialNeighbourhoodValues.set(residentialIndex, residentialValue);
            }

        }
    }

    public void createAllNeighbourhoodDataHashMap() {
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < this.neighbourhoods.size(); i++) {
            this.allNeighbourhoodsAndValues.put(this.neighbourhoods.get(i).getNeighbourhoodName(), this.allNeighbourhoodValues.get(i));
        }
        for (Map.Entry<String, Integer> entry : this.allNeighbourhoodsAndValues.entrySet()) {
            values.add(entry.getValue());
        }
        values.sort(Collections.reverseOrder());
        for (int num : values) {
            for (Map.Entry<String, Integer> entry : this.allNeighbourhoodsAndValues.entrySet()) {
                if (entry.getValue().equals(num)) {
                    this.sortedAllNeighbourhoodAndValues.put(entry.getKey(), num);
                }
            }
        }
    }

    public void createResidentialNeighbourhoodDataHashMap() {
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < this.neighbourhoods.size(); i++) {
            this.residentialNeighbourhoodsAndValues.put(this.neighbourhoods.get(i).getNeighbourhoodName(), this.residentialNeighbourhoodValues.get(i));
        }
        for (Map.Entry<String, Integer> entry : this.residentialNeighbourhoodsAndValues.entrySet()) {
            values.add(entry.getValue());
        }
        values.sort(Collections.reverseOrder());
        for (int num : values) {
            for (Map.Entry<String, Integer> entry : this.residentialNeighbourhoodsAndValues.entrySet()) {
                if (entry.getValue().equals(num)) {
                    this.sortedResidentialNeighbourhoodAndValues.put(entry.getKey(), num);
                }
            }
        }
    }

    public void createFile(String fileName) {
        try {
            File file = new File(String.valueOf(Paths.get("data", fileName)));
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void writeAllNeighbourhoodData(String fileName) {
        try {
            FileWriter writer = new FileWriter(String.valueOf(Paths.get("data", fileName)));

            Set<String> keys = this.sortedAllNeighbourhoodAndValues.keySet();
            int counter = 0;
            for (String key : keys) {
                if (counter == 10) {
                    break;
                }
                writer.write(key + ",");
                counter++;
            }
            writer.write("\n");
            counter = 0;

            for (String key : keys) {
                if (counter == 10) {
                    break;
                }
                writer.write(sortedAllNeighbourhoodAndValues.get(key) + ",");
                counter++;
            }
            counter = 0;

            writer.close();
        } catch (IOException e) {
            System.out.println("Cannot write to file.");
            e.printStackTrace();
        }
    }

    public void writeResidentialNeighbourhoodData(String fileName) {
        try {
            FileWriter writer = new FileWriter(String.valueOf(Paths.get("data", fileName)));

            Set<String> keys = this.sortedResidentialNeighbourhoodAndValues.keySet();
            int counter = 0;
            for (String key : keys) {
                if (counter == 10) {
                    break;
                }
                writer.write(key + ",");
                counter++;
            }
            writer.write("\n");
            counter = 0;

            for (String key : keys) {
                if (counter == 10) {
                    break;
                }
                writer.write(sortedResidentialNeighbourhoodAndValues.get(key) + ",");
                counter++;
            }
            counter = 0;

            writer.close();
        } catch (IOException e) {
            System.out.println("Cannot write to file.");
            e.printStackTrace();
        }
    }
}
