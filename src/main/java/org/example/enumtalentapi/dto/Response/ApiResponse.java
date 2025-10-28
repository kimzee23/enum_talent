package org.example.enumtalentapi.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String status;
    private String message;

    public ApiResponse(String success, String jobCreatedSuccessfully, JobResponse job) {
    }

    // getters/setters
}
