package com.scorer.repo.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GithubRepositoryDto {
	private String name;
	private String fullName;
	private String description;
	private Integer stargazersCount;
	private Integer forksCount;
	private Integer watchersCount;
	private Integer openIssuesCount;
	private String updatedAt;
}
