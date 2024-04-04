package com.example.cmpt305finalproject.cmpt305;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class TableViewEntry {
    private final SimpleIntegerProperty accountNum;
    private final SimpleIntegerProperty assessmentYear;
    private final SimpleLongProperty assessedValue;
    private final SimpleStringProperty suite;
    private final SimpleStringProperty houseNumber;
    private final SimpleStringProperty streetName;
    private final SimpleIntegerProperty yearBuilt;
    private final SimpleStringProperty lotSize;
    private final SimpleStringProperty garage;


    public TableViewEntry(int accountNum,
                           int assessmentYear,
                           long assessedValue,
                           String suite,
                           String houseNumber,
                           String streetName,
                           int yearBuilt,
                           String lotSize,
                           String garage) {
        this.accountNum = new SimpleIntegerProperty(accountNum);
        this.assessmentYear = new SimpleIntegerProperty(assessmentYear);
        this.assessedValue = new SimpleLongProperty(assessedValue);
        this.suite = new SimpleStringProperty(suite);
        this.houseNumber = new SimpleStringProperty(houseNumber);
        this.streetName = new SimpleStringProperty(streetName);
        this.yearBuilt = new SimpleIntegerProperty(yearBuilt);
        this.lotSize = new SimpleStringProperty(lotSize);
        this.garage = new SimpleStringProperty(garage);
    }

    public int getAccountNum() {
        return accountNum.get();
    }

    public void setAccountNum(int value) {
        accountNum.set(value);
    }

    public int getAssessmentYear() {
        return assessmentYear.get();
    }

    public void setAssessmentYear(int value) {
        assessmentYear.set(value);
    }

    public long getAssessedValue() {
        return assessedValue.get();
    }

    public void setAssessedValue(long value) {
        assessedValue.set(value);
    }

    public String getSuite() {
        return suite.get();
    }

    public void setSuite(String value) {
        suite.set(value);
    }

    public String getHouseNumber() {
        return houseNumber.get();
    }

    public void setHouseNumber(String value) {
        houseNumber.set(value);
    }

    public String getStreetName() {
        return streetName.get();
    }

    public void setStreetName(String value) {
        streetName.set(value);
    }

    public String getYearBuilt() {      // converts to a string to handle unknown values
        int value = yearBuilt.get();
        if (value == -1) {
            return "unknown";
        }
        return "" + value;
    }

    public void setYearBuilt(int value) {
        yearBuilt.set(value);
    }

    public String getLotSize() {
        return lotSize.get();
    }

    public void setLotSize(String value) {
        lotSize.set(value);
    }

    public String getGarage() {
        return garage.get();
    }

    public void setGarage(String value) {
        garage.set(value);
    }

}
