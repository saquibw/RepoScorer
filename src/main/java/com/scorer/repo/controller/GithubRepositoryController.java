package com.scorer.repo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scorer.repo.response.RepositoryResponse;
import com.scorer.repo.service.RepositoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
@Tag(name = "GitHub Repositories", description = "Fetch GitHub repositories based on language and creation date")
public class GithubRepositoryController {
	
	private final RepositoryService repositoryService;

	@Operation(summary = "Get GitHub repositories", description = "Fetches a list of repositories from GitHub based on language and creation date.")
	@GetMapping("/repositories")
    public RepositoryResponse getRepositories(
    		@Parameter(description = "Programming language to filter repositories (Java, Cobol...)") @RequestParam(name = "language") String language,
    		@Parameter(description = "Fetch repositories created after this date (YYYY-MM-DD)") @RequestParam(name = "created_after") String created_after,
    		@Parameter(description = "Page number for pagination") @RequestParam(name = "page", defaultValue = "1") Integer page
    ) {
        return repositoryService.fetchRepositories(language, created_after, page);
    }
}
