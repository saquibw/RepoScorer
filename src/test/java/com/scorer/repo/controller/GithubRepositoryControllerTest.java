package com.scorer.repo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.scorer.repo.response.RepositoryResponse;
import com.scorer.repo.service.RepositoryService;

import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(GithubRepositoryController.class)
@WithMockUser(username = "testuser", roles = "USER")
public class GithubRepositoryControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RepositoryService repositoryService;

	@Test
	void testValidInput() throws Exception {
		RepositoryResponse response = new RepositoryResponse();
		when(repositoryService.fetchRepositories("java", "2020-01-01", 1)).thenReturn(response);

		mockMvc.perform(get("/api/github/repositories")
				.param("language", "Java")
				.param("created_after", "2020-01-01")
				.param("page", "1"))
		.andExpect(status().isOk());
	}

	@Test
	void testInvalidLanguage() throws Exception {
		mockMvc.perform(get("/api/github/repositories")
				.param("language", "Jav") // Invalid language
				.param("created_after", "2020-01-01")
				.param("page", "1"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Language is not supported"));
	}
	
	@Test
    void testInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/api/github/repositories")
                .param("language", "Java")
                .param("created_after", "2020-31-12") // Invalid date format
                .param("page", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid date format or date is before 1970-01-01"));
    }

	@Test
	void testInValidCreatedAfterDateInput() throws Exception {
		mockMvc.perform(get("/api/github/repositories")
				.param("language", "Java")
				.param("created_after", "1920-01-01")
				.param("page", "1"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Invalid date format or date is before 1970-01-01"));

		mockMvc.perform(get("/api/github/repositories")
				.param("language", "Java")
				.param("created_after", "190-01-01")
				.param("page", "1"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Invalid date format or date is before 1970-01-01"));

		mockMvc.perform(get("/api/github/repositories")
				.param("language", "Java")
				.param("created_after", "1990-1-01")
				.param("page", "1"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Invalid date format or date is before 1970-01-01"));

		mockMvc.perform(get("/api/github/repositories")
				.param("language", "Java")
				.param("created_after", "1920-00-01")
				.param("page", "1"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Invalid date format or date is before 1970-01-01"));

	}

	@Test
	void testInValidPage() throws Exception {
		mockMvc.perform(get("/api/github/repositories")
				.param("language", "Java")
				.param("created_after", "2020-01-01")
				.param("page", "0"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Page number must be greater than or equal to 1"));

		mockMvc.perform(get("/api/github/repositories")
				.param("language", "Java")
				.param("created_after", "2020-01-01")
				.param("page", "-2"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Page number must be greater than or equal to 1"));
	}
	
	@Test
    void testMissingParams() throws Exception {
        mockMvc.perform(get("/api/github/repositories"))
                .andExpect(status().is5xxServerError());

        mockMvc.perform(get("/api/github/repositories")
                .param("language", "Java"))
                .andExpect(status().is5xxServerError());
    }
}
