package com.scorer.repo.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import com.scorer.repo.controller.GithubRepositoryController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GithubRepositoryIntegrationTest {
	private static final int REDIS_PORT = 6379;
    private static GenericContainer redisContainer;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private GithubRepositoryController githubRepositoryController;

    private MockMvc mockMvc;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        redisContainer = new GenericContainer("redis:6.2.4")
                .withExposedPorts(REDIS_PORT)
                .waitingFor(Wait.forListeningPort());
        redisContainer.start();

        mockMvc = MockMvcBuilders.standaloneSetup(githubRepositoryController).build();
    }

    @Test
    void testGetRepositoriesAndCacheInRedis() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/api/github/repositories")
                        .param("language", "Java")
                        .param("created_after", "2020-01-01")
                        .param("page", "1"))
                .andExpect(status().isOk());

        String redisKey = "github_repos:Java:2020-01-011";
        Object result = redisTemplate.opsForValue().get(redisKey);

        assertNotNull(result, "Data from GitHub should be stored in Redis");
    }

}
