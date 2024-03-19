package com.example.cmpt305finalproject.DAOs;

import com.example.cmpt305finalproject.cmpt305.PropertyAssessments;

import java.util.List;

public interface PropertyAssessmentDAO {
    List<String> getNeighbourhoods();
    List<String> getAssessmentClasses();
    List<PropertyAssessments> getNeighbourhoodHistoricalData(String neighbourhood);
}
