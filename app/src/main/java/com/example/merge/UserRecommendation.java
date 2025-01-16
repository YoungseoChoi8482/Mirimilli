package com.example.merge;

import java.util.List;

public class UserRecommendation {
    private String department;
    private List<String> certificates;
    private List<String> recommended;

    public UserRecommendation(String department, List<String> certificates, String recommended1, String recommended2) { }

    public UserRecommendation(String department, List<String> certificates, String recommended1, String recommended2, String recommended3) {
        this.department = department;
        this.certificates = certificates;
        this.recommended = List.of(recommended1, recommended2, recommended3);
    }

    public String getDepartment() {
        return department;
    }

    public List<String> getCertificates() {
        return certificates;
    }

    public List<String> getRecommended() {
        return recommended;
    }
}
