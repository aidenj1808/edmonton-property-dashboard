package com.example.cmpt305finalproject.cmpt305;

public class PropertyAssessment implements Comparable<PropertyAssessment> {
    private int accountNumber;
    private Address address;
    private String garage;
    private Neighbourhood neighbourhood;
    private String ward;
    private int assessedValue;
    private Location location;
    private AssessmentClasses assessmentClasses;

    public PropertyAssessment(int accountNumber, Address address, String garage,
                              Neighbourhood neighbourhood, String ward,
                              int assessedValue, Location location,
                              AssessmentClasses assessmentClasses) {
        this.accountNumber = accountNumber;
        this.address = address;
        this.garage = garage;
        this.neighbourhood = neighbourhood;
        this.ward = ward;
        this.assessedValue = assessedValue;
        this.location = location;
        this.assessmentClasses = assessmentClasses;
    }

    public int getAssessedValue() {
        return this.assessedValue;
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }

    public Address getAddress() {
        return this.address;
    }

    public Neighbourhood getNeighbourhood() {
        return this.neighbourhood;
    }

    public String getWard() {
        return this.ward;
    }

    public Location getLocation() {
        return this.location;
    }

    public AssessmentClasses getAssessmentClasses() {
        return this.assessmentClasses;
    }

    @Override
    public int compareTo(PropertyAssessment o) {
        if (o == null) {
            throw new NullPointerException("Property Assessment can't be null");
        }
        return Integer.compare(this.assessedValue, o.assessedValue);
    }

    @Override
    public String toString() {
        return "PropertyAssessment{" +
                "accountNumber=" + accountNumber +
                ", address=" + address +
                ", garage='" + garage + '\'' +
                ", neighbourhood=" + neighbourhood +
                ", ward='" + ward + '\'' +
                ", assessedValue=" + assessedValue +
                ", location=" + location +
                ", assessmentClasses=" + assessmentClasses +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PropertyAssessment otherPropertyAssessment)) {
            return false;
        }
        return this.accountNumber == otherPropertyAssessment.accountNumber;
    }

    public int hashCode() {
        return Integer.hashCode(accountNumber);
    }
}
