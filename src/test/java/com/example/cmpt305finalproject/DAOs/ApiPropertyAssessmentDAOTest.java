package com.example.cmpt305finalproject.DAOs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiPropertyAssessmentDAOTest {
    PropertyAssessmentDAO testClassCurrent;
    PropertyAssessmentDAO testClassHistorical;

    @BeforeEach
    void setUp() {
        String currentDataEndpoint = "https://data.edmonton.ca/resource/q7d6-ambg.csv";
        String historicalDataEndpoint = "https://data.edmonton.ca/resource/qi6a-xuwt.csv";

        testClassCurrent = new ApiPropertyAssessmentDAO(currentDataEndpoint);
        testClassHistorical = new ApiPropertyAssessmentDAO(historicalDataEndpoint);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getNeighbourhoods() {
        List<String> neighbourhoods = testClassCurrent.getNeighbourhoods();

        assertEquals("ABBOTTSFIELD", neighbourhoods.getFirst());
        assertEquals("HAZELDEAN", neighbourhoods.get(166));
        assertEquals("YOUNGSTOWN INDUSTRIAL", neighbourhoods.getLast());

        neighbourhoods = testClassHistorical.getNeighbourhoods();
        assertEquals("ABBOTTSFIELD", neighbourhoods.getFirst());
        assertEquals("YOUNGSTOWN INDUSTRIAL", neighbourhoods.getLast());
    }

    @Test
    void getAssessmentClasses() {
        List<String> expected = List.of(
                "FARMLAND",
                "NONRES MUNICIPAL/RES EDUCATION",
                "COMMERCIAL",
                "OTHER RESIDENTIAL",
                "RESIDENTIAL"
        );

        List<String> classes = testClassCurrent.getAssessmentClasses();
        classes.forEach(System.out::println);

    }

    @Test
    void getValuesByYear() {
        String assessmentClass = "COMMERCIAL";
        String neighbourhood = "ABBOTTSFIELD";
        int year = 2022;

        List<Long> data = testClassHistorical.getValuesByYear(neighbourhood, assessmentClass, year);
        System.out.println(data);
    }
}