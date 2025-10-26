package org.example.enumtalentapi.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "talent_profiles")
public class TalentProfile {
    @Id
    private String id;
    private String userId;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String location;
    private String profilePicture;

    private String bio;
    private String headline;
    private List<String> skills;
    private String experienceLevel;
    private String currentPosition;
    private String company;

    private String highestDegree;
    private String institution;
    private String fieldOfStudy;
    private Integer graduationYear;

    private String transcript;
    private String statementOfPurpose;
    private String resumeUrl;
    private String portfolioUrl;

    private List<String> preferredRoles;
    private String workMode;
    private String salaryExpectation;
    private String locationPreference;

    private int completeness;
    private List<String> missingFields;
    private boolean profileVisible = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}