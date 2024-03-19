package com.example.cmpt305finalproject.cmpt305;

import java.util.ArrayList;
import java.util.List;

public class AssessmentClasses {
    private List<AssessmentClass> assessmentClasses = new ArrayList<>();

    public void add(AssessmentClass assessmentClass) {
        this.assessmentClasses.add(assessmentClass);
    }

    public String toString() {
        return this.assessmentClasses.toString();
    }

    public boolean contains(String assessmentClass) {
        for (AssessmentClass aClass : this.assessmentClasses) {
            if (aClass.getClassName().equalsIgnoreCase(assessmentClass)) {
                return true;
            }
        }
        return false;
    }

    public boolean onlyContains(String assessmentClass) {
        return this.assessmentClasses.get(0).getClassName().equalsIgnoreCase(assessmentClass) && this.assessmentClasses.size() == 1;
    }
}
