package com.example.cmpt305finalproject.DAOs;

import java.util.List;
import java.util.Map;


public interface PropertyAssessmentDAO {
    List<String> getNeighbourhoods();

    List<String> getAssessmentClasses();

    List<Long> getValuesByYear(String neighbourhood, String assessmentClass, int year);

    Map<String, Long> getYearAssessedValueMedian(String neighbourhood, String assessmentClass);

    List<Long> getValuesByYearNoOutliers(String neighbourhood, String assessmentClass, int year);

    Map<String, Long> getYearAssessedValueAvg(String neighbourhood, String assessmentClass);

    Map<String, Long> getYearAvgNoOutliers(String neighbourhood, String assessmentClass);

    List<Long> removeOutliers(List<Long> data);
}
