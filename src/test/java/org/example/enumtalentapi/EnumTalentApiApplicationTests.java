package org.example.enumtalentapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.enumtalentapi.controller.TalentProfileController;
import org.example.enumtalentapi.dto.Request.TalentProfileRequest;
import org.example.enumtalentapi.dto.Response.TalentProfileResponse;
import org.example.enumtalentapi.exception.CustomException;
import org.example.enumtalentapi.service.TalentProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TalentProfileControllerTest {

	private MockMvc mockMvc;

	@Mock
	private TalentProfileService profileService;

	@InjectMocks
	private TalentProfileController profileController;

	private ObjectMapper objectMapper;
	private String userId;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(profileController).build();
		objectMapper = new ObjectMapper();
		userId = "68fb32738623940fa372fb2e";
	}

	@Test
	void getMyProfile_Success() throws Exception {
		TalentProfileResponse response = new TalentProfileResponse();
		response.setEmail("test@example.com");
		response.setVerified(true);
		response.setCompleteness(100);
		response.setMessage("Profile loaded successfully");

		when(profileService.getMyProfile(userId)).thenReturn(response);

		mockMvc.perform(get("/api/profile/talent/me")
						.param("userId", userId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("test@example.com"))
				.andExpect(jsonPath("$.verified").value(true))
				.andExpect(jsonPath("$.completeness").value(100))
				.andExpect(jsonPath("$.message").value("Profile loaded successfully"));

		verify(profileService, times(1)).getMyProfile(userId);
	}

	@Test
	void getMyProfile_UserNotFound() throws Exception {
		// Given
		when(profileService.getMyProfile(userId))
				.thenThrow(new CustomException("USER_NOT_FOUND"));

		// When & Then
		mockMvc.perform(get("/api/profile/talent/me")
						.param("userId", userId))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value("error"))
				.andExpect(jsonPath("$.message").value("USER_NOT_FOUND"));

		verify(profileService, times(1)).getMyProfile(userId);
	}

	@Test
	void getMyProfile_InternalServerError() throws Exception {
		when(profileService.getMyProfile(userId))
				.thenThrow(new RuntimeException("Database connection failed"));

		mockMvc.perform(get("/api/profile/talent/me")
						.param("userId", userId))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.status").value("error"))
				.andExpect(jsonPath("$.message").value("Unexpected error: Database connection failed"));

		verify(profileService, times(1)).getMyProfile(userId);
	}

	@Test
	void createOrUpdateProfile_Success() throws Exception {
		TalentProfileRequest request = new TalentProfileRequest();
		request.setFirstName("John");
		request.setLastName("Doe");
		request.setBio("Software developer");
		request.setTranscript("Academic transcript content");
		request.setStatementOfPurpose("Career goals statement");

		when(profileService.createOrUpdateProfile(eq(userId), any(TalentProfileRequest.class)))
				.thenReturn("Talent profile updated successfully (100% complete)");

		mockMvc.perform(post("/api/profile/talent/{userId}", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.message").value("Talent profile updated successfully (100% complete)"));

		verify(profileService, times(1)).createOrUpdateProfile(eq(userId), any(TalentProfileRequest.class));
	}

	@Test
	void createOrUpdateProfile_UserNotFound() throws Exception {
		TalentProfileRequest request = new TalentProfileRequest();
		request.setFirstName("John");
		request.setLastName("Doe");

		when(profileService.createOrUpdateProfile(eq(userId), any(TalentProfileRequest.class)))
				.thenThrow(new CustomException("USER_NOT_FOUND"));

		mockMvc.perform(post("/api/profile/talent/{userId}", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value("error"))
				.andExpect(jsonPath("$.message").value("USER_NOT_FOUND"));

		verify(profileService, times(1)).createOrUpdateProfile(eq(userId), any(TalentProfileRequest.class));
	}

	@Test
	void createOrUpdateProfile_EmailNotVerified() throws Exception {
		// Given
		TalentProfileRequest request = new TalentProfileRequest();
		request.setFirstName("John");
		request.setLastName("Doe");

		when(profileService.createOrUpdateProfile(eq(userId), any(TalentProfileRequest.class)))
				.thenThrow(new CustomException("EMAIL_NOT_VERIFIED"));

		mockMvc.perform(post("/api/profile/talent/{userId}", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value("error"))
				.andExpect(jsonPath("$.message").value("EMAIL_NOT_VERIFIED"));

		verify(profileService, times(1)).createOrUpdateProfile(eq(userId), any(TalentProfileRequest.class));
	}

	@Test
	void createOrUpdateProfile_ValidationError() throws Exception {
		TalentProfileRequest request = new TalentProfileRequest();
		request.setTranscript("a".repeat(1001));

		when(profileService.createOrUpdateProfile(eq(userId), any(TalentProfileRequest.class)))
				.thenThrow(new CustomException("Transcript too long, please shorten it thanks."));

		mockMvc.perform(post("/api/profile/talent/{userId}", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value("error"))
				.andExpect(jsonPath("$.message").value("Transcript too long, please shorten it thanks."));

		verify(profileService, times(1)).createOrUpdateProfile(eq(userId), any(TalentProfileRequest.class));
	}

	@Test
	void createOrUpdateProfile_InternalServerError() throws Exception {
		TalentProfileRequest request = new TalentProfileRequest();
		request.setFirstName("John do");

		when(profileService.createOrUpdateProfile(eq(userId), any(TalentProfileRequest.class)))
				.thenThrow(new RuntimeException("MongoDB connection timeout"));

		mockMvc.perform(post("/api/profile/talent/{userId}", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.status").value("error"))
				.andExpect(jsonPath("$.message").value("Unexpected error: MongoDB connection timeout"));

		verify(profileService, times(1)).createOrUpdateProfile(eq(userId), any(TalentProfileRequest.class));
	}
}