package com.scorer.repo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scorer.repo.dto.Language;
import com.scorer.repo.exception.InvalidRequestException;
import com.scorer.repo.exception.RateLimitExceededException;
import com.scorer.repo.response.RepositoryResponse;
import com.scorer.repo.service.RepositoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
@Tag(name = "GitHub Repositories", description = "Fetch GitHub repositories based on language and creation date")
@Validated
@Slf4j
public class GithubRepositoryController {

	private final RepositoryService repositoryService;

	@Operation(summary = "Get GitHub repositories", description = "Fetches a list of repositories from GitHub based on language and creation date.")
	@GetMapping("/repositories")
	public RepositoryResponse getRepositories(
			@Parameter(description = "Programming language to filter repositories (Java, Cobol...)") @RequestParam(name = "language") @NotBlank(message = "Language is required and cannot be empty") String language,
			@Parameter(description = "Fetch repositories created after this date (YYYY-MM-DD)") @RequestParam(name = "created_after") @NotBlank(message = "Date is required and cannot be empty") String created_after,
			@Parameter(description = "Page number for pagination") @RequestParam(name = "page", defaultValue = "1") Integer page
			) throws InvalidRequestException, RateLimitExceededException {

		if (!isValidDate(created_after)) {
			throw new InvalidRequestException("Invalid date format or date is before 1970-01-01");
		}
		if (page < 1) {
			throw new InvalidRequestException("Page number must be greater than or equal to 1");
		}

		Language lang = Language.fromString(language.trim().toLowerCase());
		if (lang == null) {
			throw new InvalidRequestException("Language is not supported");
		}
		return repositoryService.fetchRepositories(language, created_after, page);
	}

	private boolean isValidDate(String date) {
		try {
			LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			return parsedDate.isAfter(LocalDate.of(1970, 1, 1));
		} catch (DateTimeParseException e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}
}
