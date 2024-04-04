package com.example.cmpt305finalproject.DAOs;

import com.example.cmpt305finalproject.cmpt305.PropertyAssessments;

import java.util.List;
import java.util.Map;

public interface PropertyAssessmentDAO {
    List<String> getNeighbourhoods();
    List<String> getAssessmentClasses();
    List<PropertyAssessments> getNeighbourhoodData(String neighbourhood);
    List<Long> getValuesByYear(String neighbourhood, String assessmentClass, int year);
    Map<String, Long> getYearAssessedValueMedian(String neighbourhood, String assessmentClass);


}
