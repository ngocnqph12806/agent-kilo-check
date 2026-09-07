package com.skillseed.session.dto;

import jakarta.validation.constraints.Size;

public class ReportIssueRequest {

    @Size(max = 1000)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
