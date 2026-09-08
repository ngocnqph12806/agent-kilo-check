package com.skillseed.session.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ReportIssueRequest {

    @NotBlank
    @Pattern(regexp = "audio|video|network|whiteboard|other",
            message = "category must be one of audio, video, network, whiteboard, other")
    private String category;

    @NotBlank
    @Size(max = 1000)
    private String description;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
