package org.example.enumtalentapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enumtalentapi.dto.Request.TalentProfileRequest;
import org.example.enumtalentapi.dto.Response.ApiResponse;
import org.example.enumtalentapi.dto.Response.TalentProfileResponse;
import org.example.enumtalentapi.exception.CustomException;
import org.example.enumtalentapi.service.TalentProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/profile/talent")
@RequiredArgsConstructor
public class TalentProfileController {

    private final TalentProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<TalentProfileResponse> getMyProfile(@RequestParam String userId) {
        TalentProfileResponse profile = profileService.getMyProfile(userId);
        return ResponseEntity.ok(profile);
    }
    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse> createOrUpdateProfile(
            @PathVariable String userId,
            @RequestBody TalentProfileRequest request
    ) {
        return handleRequest(
                () -> profileService.createOrUpdateProfile(userId, request),
                "Profile created or updated successfully"
        );
    }

    private <T> ResponseEntity<ApiResponse> handleRequest(ServiceAction<T> action, String successMessage) {
        try {
            T result = action.execute();
            String message = result instanceof String ? (String) result : successMessage;
            return ResponseEntity.ok(new ApiResponse("success", message));
        } catch (CustomException e) {
            log.warn("Business error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error occurred: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Unexpected error: " + e.getMessage()));
        }
    }

    @FunctionalInterface
    private interface ServiceAction<T> {
        T execute() throws Exception;
    }
}
