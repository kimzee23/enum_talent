package org.example.enumtalentapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enumtalentapi.dto.Request.TalentProfileRequest;
import org.example.enumtalentapi.dto.Response.TalentProfileResponse;
import org.example.enumtalentapi.entity.TalentProfile;
import org.example.enumtalentapi.entity.User;
import org.example.enumtalentapi.exception.CustomException;
import org.example.enumtalentapi.repository.TalentProfileRepository;
import org.example.enumtalentapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TalentProfileService {

    private final TalentProfileRepository profileRepository;
    private final UserRepository userRepository;

    public String createOrUpdateProfile(String userId, TalentProfileRequest request) {
        User user = getVerifiedUser(userId);
        TalentProfile profile = getOrCreateProfile(userId);

        validateProfileContent(request);
        updateProfileFields(profile, request);
        updateCompleteness(profile);

        profileRepository.save(profile);

        return String.format(
                "Talent profile updated successfully (%d%% complete)",
                profile.getCompleteness()
        );
    }

    public TalentProfileResponse getMyProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND"));

        TalentProfileResponse response = new TalentProfileResponse();
        response.setEmail(user.getEmail());
        response.setVerified(user.isVerified());

        Optional<TalentProfile> profileOpt = profileRepository.findByUserId(userId);

        if (profileOpt.isPresent()) {
            mapProfileToResponse(profileOpt.get(), response);
        } else {
            populateEmptyProfileResponse(response);
        }

        return response;
    }

    private User getVerifiedUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND"));

        if (!user.isVerified()) {
            throw new CustomException("EMAIL_NOT_VERIFIED");
        }

        return user;
    }

    private TalentProfile getOrCreateProfile(String userId) {
        return profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    TalentProfile profile = new TalentProfile();
                    profile.setUserId(userId);
                    return profile;
                });
    }

    private void validateProfileContent(TalentProfileRequest request) {
        validateLength(request.getTranscript(), 1000, "Transcript too long, please shorten it.");
        validateLength(request.getStatementOfPurpose(), 2000, "Statement of Purpose too long, please shorten it.");
        validateLength(request.getBio(), 500, "Bio too long, please shorten it.");
    }

    private void validateLength(String field, int maxLength, String message) {
        if (field != null && field.length() > maxLength) {
            throw new CustomException(message);
        }
    }

    private void updateProfileFields(TalentProfile profile, TalentProfileRequest req) {
        profile.setFirstName(req.getFirstName());
        profile.setLastName(req.getLastName());
        profile.setPhone(req.getPhone());
        profile.setLocation(req.getLocation());
        profile.setProfilePicture(req.getProfilePicture());
        profile.setBio(req.getBio());
        profile.setHeadline(req.getHeadline());
        profile.setSkills(req.getSkills());
        profile.setExperienceLevel(req.getExperienceLevel());
        profile.setCurrentPosition(req.getCurrentPosition());
        profile.setCompany(req.getCompany());
        profile.setHighestDegree(req.getHighestDegree());
        profile.setInstitution(req.getInstitution());
        profile.setFieldOfStudy(req.getFieldOfStudy());
        profile.setGraduationYear(req.getGraduationYear());
        profile.setTranscript(req.getTranscript());
        profile.setStatementOfPurpose(req.getStatementOfPurpose());
        profile.setResumeUrl(req.getResumeUrl());
        profile.setPortfolioUrl(req.getPortfolioUrl());
        profile.setPreferredRoles(req.getPreferredRoles());
        profile.setWorkMode(req.getWorkMode());
        profile.setSalaryExpectation(req.getSalaryExpectation());
        profile.setLocationPreference(req.getLocationPreference());
    }

    private void updateCompleteness(TalentProfile profile) {
        List<String> missingFields = new ArrayList<>();
        int totalFields = 10;
        int completed = 0;

        completed += track(profile.getFirstName(), "firstName", missingFields);
        completed += track(profile.getLastName(), "lastName", missingFields);
        completed += track(profile.getBio(), "bio", missingFields);
        completed += track(profile.getHeadline(), "headline", missingFields);
        completed += trackList(profile.getSkills(), "skills", missingFields);
        completed += track(profile.getExperienceLevel(), "experienceLevel", missingFields);
        completed += track(profile.getLocation(), "location", missingFields);
        completed += track(profile.getTranscript(), "transcript", missingFields);
        completed += track(profile.getStatementOfPurpose(), "statementOfPurpose", missingFields);
        completed += track(profile.getResumeUrl(), "resumeUrl", missingFields);

        int completeness = (int) ((completed / (double) totalFields) * 100);
        profile.setCompleteness(completeness);
        profile.setMissingFields(missingFields);
    }

    private int track(String value, String field, List<String> missing) {
        if (isNotEmpty(value)) return 1;
        missing.add(field);
        return 0;
    }

    private int trackList(List<?> value, String field, List<String> missing) {
        if (value != null && !value.isEmpty()) return 1;
        missing.add(field);
        return 0;
    }

    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private void populateEmptyProfileResponse(TalentProfileResponse response) {
        response.setCompleteness(0);
        response.setMissingFields(List.of(
                "firstName", "lastName", "bio", "headline", "skills",
                "experienceLevel", "location", "transcript", "statementOfPurpose"
        ));
        response.setMessage("Profile not started. Complete your profile to get started!");
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
