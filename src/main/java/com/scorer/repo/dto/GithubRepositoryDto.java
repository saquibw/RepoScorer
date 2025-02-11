package com.scorer.repo.dto;

import lombok.Data;

@Data
public class GithubRepositoryDto {
	private String name;
	private String full_name;
	private String description;
	private Integer stargazers_count;
	private Integer forks_count;
	private Integer watchers_count;
	private Integer open_issues_count;
	private String updated_at;
}
