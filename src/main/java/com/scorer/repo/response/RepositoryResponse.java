package com.scorer.repo.response;

import java.util.List;

import com.scorer.repo.dto.RepositoryDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryResponse {
	private int totalCount;
	private List<RepositoryDto> repositories;
}
