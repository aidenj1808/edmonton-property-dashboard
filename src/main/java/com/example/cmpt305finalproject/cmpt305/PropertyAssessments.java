package com.example.cmpt305finalproject.cmpt305;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PropertyAssessments {
    private List<PropertyAssessment> propertyAssessments = new ArrayList<>();

    public  PropertyAssessments() {
    }
    public PropertyAssessments(String fileName) throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get("src", fileName));
        reader.readLine();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",", -1);
            Address address = new Address(values[1], values[2], values[3]);
            Neighbourhood neighbourhood = new Neighbourhood(values[6], values[5]);
            Location location = new Location(Double.parseDouble(values[9]), Double.parseDouble(values[10]));
            AssessmentClasses assessmentClasses = new AssessmentClasses();
            for (int i = 12; i <= 14; i++) {
                String className = values[i + 3];
                String classPercent = values[i];
                if (!className.isEmpty()) {
                    AssessmentClass assessmentClass = new AssessmentClass(className, classPercent);
                    assessmentClasses.add(assessmentClass);
                }


            }
            int accountNumber = Integer.parseInt(values[0]);
            String garage = values[4];
            String ward = values[7];
            int assessedValue = Integer.parseInt(values[8]);
            PropertyAssessment propertyAssessment = new PropertyAssessment(accountNumber, address, garage, neighbourhood,
                    ward, assessedValue, location, assessmentClasses);
            this.propertyAssessments.add(propertyAssessment);
        }
    }

    public void add(PropertyAssessment propertyAssessment) {
        this.propertyAssessments.add(propertyAssessment);
    }

    public PropertyAssessment get(int index) {
        return this.propertyAssessments.get(index);
    }

    public int getSize() {
        return propertyAssessments.size();
    }

    public int getMinAssessedValue() {
        PropertyAssessment min = this.propertyAssessments.get(0);
        for (int i = 1; i < this.propertyAssessments.size(); i++) {
            if (this.propertyAssessments.get(i).compareTo(min) < 0) {
                min = this.propertyAssessments.get(i);
            }
        }
        return min.getAssessedValue();
    }

    public int getMaxAssessedValue() {
        PropertyAssessment max = this.propertyAssessments.get(0);
        for (int i = 1; i < this.propertyAssessments.size(); i++) {
            if (this.propertyAssessments.get(i).compareTo(max) > 0) {
                max = this.propertyAssessments.get(i);
            }
        }
        return max.getAssessedValue();
    }

    public int getRangeAssessedValue() {
        PropertyAssessment min = this.propertyAssessments.get(0);
        PropertyAssessment max = this.propertyAssessments.get(0);
        for (int i = 1; i < this.propertyAssessments.size(); i++) {
            if (this.propertyAssessments.get(i).compareTo(min) < 0) {
                min = this.propertyAssessments.get(i);
            } else if (this.propertyAssessments.get(i).compareTo(max) > 0) {
                max = this.propertyAssessments.get(i);
            }
        }
        return max.getAssessedValue() - min.getAssessedValue();
    }

    public int getMeanAssessedValue() {
        BigDecimal total = BigDecimal.valueOf(0);
        for (PropertyAssessment propertyAssessment: this.propertyAssessments) {
            total = total.add(BigDecimal.valueOf(propertyAssessment.getAssessedValue()));
        }
        return total.divide(BigDecimal.valueOf(this.getSize()), RoundingMode.DOWN).intValue();
    }

    public int getMedianAssessedValue() {
        Collections.sort(this.propertyAssessments);
        if (this.getSize() % 2 == 1) {
            return this.propertyAssessments.get(this.getSize() / 2).getAssessedValue();
        }
        int first = this.propertyAssessments.get((this.getSize() / 2) - 1).getAssessedValue();
        int second = this.propertyAssessments.get(this.getSize() / 2).getAssessedValue();
        return (first + second) / 2;

    }

    public PropertyAssessment getPropertyAssessment(int accountNumber) {
        for (PropertyAssessment propertyAssessment: this.propertyAssessments) {
            if (accountNumber == propertyAssessment.getAccountNumber()) {
                return propertyAssessment;
            }
        }
        return null;
    }

    public PropertyAssessments getNeighbourhoodPropertyAssessments(String neighbourhood) {
        PropertyAssessments neighbourhoodPropertyAssessments = new PropertyAssessments();
        for (PropertyAssessment propertyAssessment : this.propertyAssessments) {
            if (propertyAssessment.getNeighbourhood().getNeighbourhoodName().equalsIgnoreCase(neighbourhood)) {
                neighbourhoodPropertyAssessments.add(propertyAssessment);
            }
        }
        return neighbourhoodPropertyAssessments;
    }

    public PropertyAssessments getClassPropertyAssessments(String assessmentClass) {
        PropertyAssessments classPropertyAssessments = new PropertyAssessments();
        for (PropertyAssessment propertyAssessment : this.propertyAssessments) {
            if (propertyAssessment.getAssessmentClasses().contains(assessmentClass)) {
                classPropertyAssessments.add(propertyAssessment);
            }
        }
        return classPropertyAssessments;
    }

    public void sortPropertyAssessments(String order) {
        if (order.equals("asc")) {
            Collections.sort(this.propertyAssessments);
        } else if (order.equals("desc")) {
            Collections.sort(this.propertyAssessments, Collections.reverseOrder());
        }

    }

    public boolean isEmpty() {
        return this.getSize() == 0;
    }
}
