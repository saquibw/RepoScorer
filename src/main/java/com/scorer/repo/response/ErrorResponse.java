package com.scorer.repo.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ErrorResponse {
	private final LocalDateTime timestamp;
    private final String message;

    public ErrorResponse(String message) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }
}
