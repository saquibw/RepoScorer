package com.scorer.repo.exception;

public class RateLimitExceededException extends Exception{
	private static final long serialVersionUID = 1L;

	public RateLimitExceededException(String message) {
        super(message);
    }
}
