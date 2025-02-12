package com.scorer.repo.response;

import java.util.List;

import com.scorer.repo.dto.GithubRepositoryDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GithubRepositoryResponse {
	private int total_count; 
	private List<GithubRepositoryDto> items;
}
