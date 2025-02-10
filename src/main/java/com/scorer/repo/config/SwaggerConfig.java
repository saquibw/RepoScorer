package com.scorer.repo.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(title = "GitHub Repository API", version = "1.0", description = "API for fetching GitHub repositories")
)
public class SwaggerConfig {

}
