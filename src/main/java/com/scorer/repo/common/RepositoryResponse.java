package com.scorer.repo.common;

import java.util.List;

import com.scorer.repo.common.RepositoryDto;

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
