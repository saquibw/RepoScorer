package com.scorer.repo.response;

import java.util.List;

import com.scorer.repo.dto.GithubRepositoryDto;

import lombok.Data;

@Data
public class GithubRepositoryResponse {
	private int total_count; 
	private List<GithubRepositoryDto> items;
}
