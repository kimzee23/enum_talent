package org.example.enumtalentapi.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "jobs")
public class Job {
    @Id
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
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String postedBy;
}