package com.example.cmpt305finalproject.cmpt305;

import java.util.Objects;

public class AssessmentClass {
    private String className;
    private String classPercent;

    public AssessmentClass(String className, String classPercent) {
        this.className = className;
        this.classPercent = classPercent;
    }

    public String getClassName() {
        return this.className;
    }

    @Override
    public String toString() {
        return this.className + " " + this.classPercent + "%";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AssessmentClass other)) {
            return false;
        }
        return obj.toString().equals(this.toString());
    }

    public int hashCode() {
        return Objects.hash(this.className, this.classPercent);
    }
}
