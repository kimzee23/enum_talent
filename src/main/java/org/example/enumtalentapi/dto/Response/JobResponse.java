package org.example.enumtalentapi.dto.Response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobResponse {
    private String id;
    private String title;
    private String company;
    private String description;
    private String location;
    private String workMode;
    private String employmentType;
    private List<String> requiredSkills;
    private List<String> preferredSkills;
    private Double salaryMin;
    private Double salaryMax;
    private String salaryCurrency;
    private Integer experienceLevel;
    private String applicationUrl;
    private String contactEmail;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String postedBy;
}