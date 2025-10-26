package org.example.enumtalentapi.service;

import lombok.RequiredArgsConstructor;
import org.example.enumtalentapi.dto.TalentProfileRequest;
import org.example.enumtalentapi.dto.TalentProfileResponse;
import org.example.enumtalentapi.entity.TalentProfile;
import org.example.enumtalentapi.entity.User;
import org.example.enumtalentapi.exception.CustomException;
import org.example.enumtalentapi.repository.TalentProfileRepository;
import org.example.enumtalentapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TalentProfileService {

    private final TalentProfileRepository profileRepository;
    private final UserRepository userRepository;

    public String createOrUpdateProfile(String userId, TalentProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND"));

        if (!user.isVerified()) {
            throw new CustomException("EMAIL_NOT_VERIFIED");
        }

        TalentProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    TalentProfile newProfile = new TalentProfile();
                    newProfile.setUserId(userId);
                    return newProfile;
                });

        if (request.getTranscript() != null && request.getTranscript().length() > 1000) {
            throw new CustomException("Transcript too long, please shorten it thanks.");
        }
        if (request.getStatementOfPurpose() != null && request.getStatementOfPurpose().length() > 2000) {
            throw new CustomException("Statement of Purpose too long, please shorten it thanks.");
        }
        if (request.getBio() != null && request.getBio().length() > 500) {
            throw new CustomException("Bio too long, please shorten it thanks.");
        }

        updateProfileFields(profile, request);

        calculateCompleteness(profile);

        profileRepository.save(profile);

        return "Talent profile updated successfully (" + profile.getCompleteness() + "% complete)";
    }

    private void updateProfileFields(TalentProfile profile, TalentProfileRequest request) {
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhone(request.getPhone());
        profile.setLocation(request.getLocation());
        profile.setProfilePicture(request.getProfilePicture());

        profile.setBio(request.getBio());
        profile.setHeadline(request.getHeadline());
        profile.setSkills(request.getSkills());
        profile.setExperienceLevel(request.getExperienceLevel());
        profile.setCurrentPosition(request.getCurrentPosition());
        profile.setCompany(request.getCompany());

        profile.setHighestDegree(request.getHighestDegree());
        profile.setInstitution(request.getInstitution());
        profile.setFieldOfStudy(request.getFieldOfStudy());
        profile.setGraduationYear(request.getGraduationYear());

        profile.setTranscript(request.getTranscript());
        profile.setStatementOfPurpose(request.getStatementOfPurpose());
        profile.setResumeUrl(request.getResumeUrl());
        profile.setPortfolioUrl(request.getPortfolioUrl());

        profile.setPreferredRoles(request.getPreferredRoles());
        profile.setWorkMode(request.getWorkMode());
        profile.setSalaryExpectation(request.getSalaryExpectation());
        profile.setLocationPreference(request.getLocationPreference());
    }

    private void calculateCompleteness(TalentProfile profile) {
        List<String> missing = new ArrayList<>();
        int totalFields = 10;
        int completedFields = 0;

        if (isNotEmpty(profile.getFirstName())) completedFields++;
        else missing.add("firstName");

        if (isNotEmpty(profile.getLastName())) completedFields++;
        else missing.add("lastName");

        if (isNotEmpty(profile.getBio())) completedFields++;
        else missing.add("bio");

        if (isNotEmpty(profile.getHeadline())) completedFields++;
        else missing.add("headline");

        if (profile.getSkills() != null && !profile.getSkills().isEmpty()) completedFields++;
        else missing.add("skills");

        if (isNotEmpty(profile.getExperienceLevel())) completedFields++;
        else missing.add("experienceLevel");

        if (isNotEmpty(profile.getLocation())) completedFields++;
        else missing.add("location");

        if (isNotEmpty(profile.getTranscript())) completedFields++;
        else missing.add("transcript");

        if (isNotEmpty(profile.getStatementOfPurpose())) completedFields++;
        else missing.add("statementOfPurpose");

        if (isNotEmpty(profile.getResumeUrl())) completedFields++;
        else missing.add("resumeUrl");

        int completeness = (int) ((completedFields / (double) totalFields) * 100);
        profile.setCompleteness(completeness);
        profile.setMissingFields(missing);
    }

    private boolean isNotEmpty(String field) {
        return field != null && !field.trim().isEmpty();
    }

    public TalentProfileResponse getMyProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND"));

        // Always return user email, even if profile doesn't exist
        TalentProfileResponse response = new TalentProfileResponse();
        response.setEmail(user.getEmail());
        response.setVerified(user.isVerified());

        // Try to find profile
        Optional<TalentProfile> profileOpt = profileRepository.findByUserId(userId);

        if (profileOpt.isPresent()) {
            TalentProfile profile = profileOpt.get();
            mapProfileToResponse(profile, response);
        } else {
            response.setCompleteness(0);
            response.setMissingFields(List.of("firstName", "lastName", "bio", "headline", "skills",
                    "experienceLevel", "location", "transcript", "statementOfPurpose"));
            response.setMessage("Profile not started. Complete your profile to get started!");
        }

        return response;
    }

    private void mapProfileToResponse(TalentProfile profile, TalentProfileResponse response) {
        response.setFirstName(profile.getFirstName());
        response.setLastName(profile.getLastName());
        response.setPhone(profile.getPhone());
        response.setLocation(profile.getLocation());
        response.setProfilePicture(profile.getProfilePicture());

        response.setBio(profile.getBio());
        response.setHeadline(profile.getHeadline());
        response.setSkills(profile.getSkills());
        response.setExperienceLevel(profile.getExperienceLevel());
        response.setCurrentPosition(profile.getCurrentPosition());
        response.setCompany(profile.getCompany());

        response.setHighestDegree(profile.getHighestDegree());
        response.setInstitution(profile.getInstitution());
        response.setFieldOfStudy(profile.getFieldOfStudy());
        response.setGraduationYear(profile.getGraduationYear());

        response.setTranscript(profile.getTranscript());
        response.setStatementOfPurpose(profile.getStatementOfPurpose());
        response.setResumeUrl(profile.getResumeUrl());
        response.setPortfolioUrl(profile.getPortfolioUrl());

        response.setPreferredRoles(profile.getPreferredRoles());
        response.setWorkMode(profile.getWorkMode());
        response.setSalaryExpectation(profile.getSalaryExpectation());
        response.setLocationPreference(profile.getLocationPreference());

        response.setCompleteness(profile.getCompleteness());
        response.setMissingFields(profile.getMissingFields());
        response.setProfileVisible(profile.isProfileVisible());
        response.setMessage("Profile loaded successfully");
    }
}