package org.example.enumtalentapi.controller;

import lombok.RequiredArgsConstructor;

import org.example.enumtalentapi.dto.Request.JobRequest;
import org.example.enumtalentapi.dto.Response.ApiResponse;
import org.example.enumtalentapi.dto.Response.JobResponse;
import org.example.enumtalentapi.exception.CustomException;
import org.example.enumtalentapi.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<ApiResponse> createJob(
            @RequestBody JobRequest request,
            @RequestParam String userId) {
        try {
            JobResponse job = jobService.createJob(request, userId);
            return ResponseEntity.ok(new ApiResponse("success", "Job created successfully", job));
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("error", "Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String workMode,
            @RequestParam(required = false) String employmentType) {
        try {
            List<JobResponse> jobs = jobService.searchJobs(search, location, workMode, employmentType);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("error", "Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<?> getJobById(@PathVariable String jobId) {
        try {
            JobResponse job = jobService.getJobById(jobId);
            return ResponseEntity.ok(job);
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("error", "Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping("/skills")
    public ResponseEntity<?> getJobsBySkills(@RequestParam List<String> skills) {
        try {
            List<JobResponse> jobs = jobService.getJobsBySkills(skills);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("error", "Unexpected error: " + e.getMessage()));
        }
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<ApiResponse> updateJob(
            @PathVariable String jobId,
            @RequestBody JobRequest request,
            @RequestParam String userId) {
        try {
            JobResponse job = jobService.updateJob(jobId, request, userId);
            return ResponseEntity.ok(new ApiResponse("success", "Job updated successfully", job));
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("error", "Unexpected error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<ApiResponse> deleteJob(
            @PathVariable String jobId,
            @RequestParam String userId) {
        try {
            String message = jobService.deleteJob(jobId, userId);
            return ResponseEntity.ok(new ApiResponse("success", message));
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("error", "Unexpected error: " + e.getMessage()));
        }
    }
}