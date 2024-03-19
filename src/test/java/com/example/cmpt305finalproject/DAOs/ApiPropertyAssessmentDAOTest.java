package com.example.cmpt305finalproject.DAOs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiPropertyAssessmentDAOTest {
    PropertyAssessmentDAO testClass;

    @BeforeEach
    void setUp() {
        String currentDataEndpoint = "https://data.edmonton.ca/resource/q7d6-ambg.csv";
        String historicalDataEndpoint = "https://data.edmonton.ca/api/odata/v4/qi6a-xuwt.csv";

        testClass = new ApiPropertyAssessmentDAO(currentDataEndpoint, historicalDataEndpoint);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getNeighbourhoods() {
        List<String> neighbourhoods = testClass.getNeighbourhoods();
        neighbourhoods.forEach(x -> System.out.println(x));
    }

    @Test
    void getAssessmentClasses() {
    }

    @Test
    void getNeighbourhoodHistoricalData() {
    }
}