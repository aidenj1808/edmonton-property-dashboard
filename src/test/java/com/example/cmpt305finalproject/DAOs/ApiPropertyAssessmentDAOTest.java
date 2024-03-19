package com.example.cmpt305finalproject.DAOs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

        testClass = new ApiPropertyAssessmentDAO(currentDataEndpoint);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getNeighbourhoods() {
        List<String> neighbourhoods = testClass.getNeighbourhoods();
//        neighbourhoods.forEach(x -> System.out.println(x));

        assertEquals("ABBOTTSFIELD", neighbourhoods.getFirst());
        assertEquals("HAZELDEAN", neighbourhoods.get(166));
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

        List<String> classes = testClass.getAssessmentClasses();
        classes.forEach(System.out::println);

    }

    @Test
    void getNeighbourhoodHistoricalData() {
    }
}