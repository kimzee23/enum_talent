package org.example.enumtalentapi.service;

import lombok.RequiredArgsConstructor;

import org.example.enumtalentapi.dto.Request.JobRequest;
import org.example.enumtalentapi.dto.Response.JobResponse;
import org.example.enumtalentapi.entity.Job;
import org.example.enumtalentapi.exception.CustomException;
import org.example.enumtalentapi.repository.JobRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public JobResponse createJob(JobRequest request, String postedBy) {
        Job job = new Job();
        mapRequestToJob(request, job);
        job.setPostedBy(postedBy);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());

        Job savedJob = jobRepository.save(job);
        return mapJobToResponse(savedJob);
    }

    public List<JobResponse> getAllActiveJobs() {
        return jobRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapJobToResponse)
                .collect(Collectors.toList());
    }

    public List<JobResponse> searchJobs(String searchTerm, String location, String workMode, String employmentType) {
        List<Job> jobs;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            jobs = jobRepository.searchJobs(searchTerm.trim());
        } else {
            jobs = jobRepository.findByIsActiveTrue();
        }

        // Apply filters
        if (location != null && !location.trim().isEmpty()) {
            jobs = jobs.stream()
                    .filter(job -> job.getLocation() != null &&
                            job.getLocation().toLowerCase().contains(location.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (workMode != null && !workMode.trim().isEmpty()) {
            jobs = jobs.stream()
                    .filter(job -> workMode.equals(job.getWorkMode()))
                    .collect(Collectors.toList());
        }

        if (employmentType != null && !employmentType.trim().isEmpty()) {
            jobs = jobs.stream()
                    .filter(job -> employmentType.equals(job.getEmploymentType()))
                    .collect(Collectors.toList());
        }

        return jobs.stream()
                .map(this::mapJobToResponse)
                .collect(Collectors.toList());
    }

    public List<JobResponse> getJobsBySkills(List<String> skills) {
        return jobRepository.findBySkills(skills)
                .stream()
                .map(this::mapJobToResponse)
                .collect(Collectors.toList());
    }

    public JobResponse getJobById(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException("JOB_NOT_FOUND"));
        return mapJobToResponse(job);
    }

    public JobResponse updateJob(String jobId, JobRequest request, String userId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException("JOB_NOT_FOUND"));

        if (!job.getPostedBy().equals(userId)) {
            throw new CustomException("NOT_AUTHORIZED");
        }

        mapRequestToJob(request, job);
        job.setUpdatedAt(LocalDateTime.now());

        Job updatedJob = jobRepository.save(job);
        return mapJobToResponse(updatedJob);
    }

    public String deleteJob(String jobId, String userId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException("JOB_NOT_FOUND"));

        if (!job.getPostedBy().equals(userId)) {
            throw new CustomException("NOT_AUTHORIZED");
        }

        jobRepository.delete(job);
        return "Job deleted successfully";
    }

    private void mapRequestToJob(JobRequest request, Job job) {
        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setWorkMode(request.getWorkMode());
        job.setEmploymentType(request.getEmploymentType());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setPreferredSkills(request.getPreferredSkills());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setSalaryCurrency(request.getSalaryCurrency());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setApplicationUrl(request.getApplicationUrl());
        job.setContactEmail(request.getContactEmail());
    }

    private JobResponse mapJobToResponse(Job job) {
        JobResponse response = new JobResponse();
        response.setId(job.getId());
        response.setTitle(job.getTitle());
        response.setCompany(job.getCompany());
        response.setDescription(job.getDescription());
        response.setLocation(job.getLocation());
        response.setWorkMode(job.getWorkMode());
        response.setEmploymentType(job.getEmploymentType());
        response.setRequiredSkills(job.getRequiredSkills());
        response.setPreferredSkills(job.getPreferredSkills());
        response.setSalaryMin(job.getSalaryMin());
        response.setSalaryMax(job.getSalaryMax());
        response.setSalaryCurrency(job.getSalaryCurrency());
        response.setExperienceLevel(job.getExperienceLevel());
        response.setApplicationUrl(job.getApplicationUrl());
        response.setContactEmail(job.getContactEmail());
        response.setIsActive(job.getIsActive());
        response.setCreatedAt(job.getCreatedAt());
        response.setPostedBy(job.getPostedBy());
        return response;
    }
}