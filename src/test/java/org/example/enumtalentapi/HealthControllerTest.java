package org.example.enumtalentapi;

import org.example.enumtalentapi.controller.HealthController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HealthControllerTest {

    @InjectMocks
    private HealthController healthController;

    @Test
    void health_ReturnsOkStatus() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(healthController).build();

        mockMvc.perform(get("/.well-known/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void health_ReturnsCorrectJsonStructure() throws Exception {

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(healthController).build();
        mockMvc.perform(get("/.well-known/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").isString())
                .andExpect(jsonPath("$.status").value("ok"));
    }
}