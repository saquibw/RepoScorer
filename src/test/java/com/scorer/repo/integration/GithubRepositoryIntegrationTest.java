package com.scorer.repo.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GithubRepositoryIntegrationTest {
	private static final int REDIS_PORT = 6379;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;


	@BeforeAll
	static void startRedisContainer() {
		try {
			GenericContainer<?> redisContainer =
					new GenericContainer<>(DockerImageName.parse("redis:7.0"))
					.withExposedPorts(REDIS_PORT)
					.waitingFor(Wait.forLogMessage(".*Ready to accept connections.*\\n", 1));
			redisContainer.start();
			System.setProperty("spring.data.redis.host", redisContainer.getHost());
			System.setProperty("spring.data.redis.port", redisContainer.getFirstMappedPort().toString());
		} catch (Exception e) {
			System.out.println("Error starting Redis container: " + e.getMessage());
		}
	}

//	@Test
//	void testGetRepositoriesAndCacheInRedis() throws Exception {
////		HttpHeaders headers = new HttpHeaders();
////		headers.setBasicAuth("user", "password");
////		HttpEntity<String> entity = new HttpEntity<>(headers); bv
////		HttpStatusCode statusCode = restTemplate.getForEntity("http://localhost:" + port + "/api/github/repositories?language=Java&created_after=2020-01-01&page=1", String.class)
////				.getStatusCode();
////		System.out.println(statusCode);
//		
//		String username = "user"; // Replace with the value of app.security.user
//	    String password = "password";
//		
//		MultiValueMap<String, String> loginData = new LinkedMultiValueMap<>();
//	    loginData.add("username", username);
//	    loginData.add("password", password);
//
//	    // Create a HttpEntity with login data (form data)
//	    HttpEntity<MultiValueMap<String, String>> loginEntity = new HttpEntity<>(loginData, new HttpHeaders());
//
//	    // Perform login via POST request to the login endpoint
//	    ResponseEntity<String> loginResponse = restTemplate.postForEntity("http://localhost:" + port + "/login", loginEntity, String.class);
//	    
//	    // Check if login was successful
//	    assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
//
//	    // Now that the user is authenticated, proceed with the actual test for repository retrieval
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.add(HttpHeaders.COOKIE, loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE).get(0));
//	    HttpEntity<String> entity = new HttpEntity<>(headers);
//	    
////	    HttpStatusCode statusCode = restTemplate.getForEntity("http://localhost:" + port + "/api/github/repositories?language=Java&created_after=2020-01-01&page=1", String.class)
////	            .getStatusCode();
//	    ResponseEntity<String> response = restTemplate.exchange(
//	            "http://localhost:" + port + "/api/github/repositories?language=Java&created_after=2020-01-01&page=1",
//	            HttpMethod.GET, entity, String.class
//	        );
//	    //System.out.println(statusCode);
//		
//	    assertEquals(HttpStatus.OK, response.getStatusCode()); 
//		//assert(statusCode.is2xxSuccessful());
//		
//		 Set<String> allKeys = redisTemplate.keys("*");
//
//	        // Print all the keys in Redis
//	        System.out.println("All Redis Keys: ");
//	        allKeys.forEach(System.out::println);
//
//		String redisKey = "github_repos:Java:2020-01-011";
//		Object result = redisTemplate.opsForValue().get(redisKey);
//		
//		System.out.println("Redis Key: " + redisKey);
//	    System.out.println("Redis Value: " + result);
//
//		assertNotNull(result, "Data from GitHub should be stored in Redis");
//	}
	
	@Test
	void testGetRepositoriesAndCacheInRedis() throws Exception {
	    String username = "user"; // Use the correct credentials
	    String password = "password";

	    MultiValueMap<String, String> loginData = new LinkedMultiValueMap<>();
	    loginData.add("username", username);
	    loginData.add("password", password);

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

	    HttpEntity<MultiValueMap<String, String>> loginEntity = new HttpEntity<>(loginData, headers);

	    // Perform login via POST request to /login
	    ResponseEntity<String> loginResponse = restTemplate.postForEntity(
	            "http://localhost:" + port + "/login", 
	            loginEntity, 
	            String.class
	    );
	    System.out.println(loginResponse);

	    // Ensure login was successful
	    assertEquals(HttpStatus.FOUND, loginResponse.getStatusCode(), "Login should return a redirect (302 Found)");

	    // Extract session cookie
	    List<String> cookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
	    System.out.println("loginResponse.getHeaders()**********");
	    System.out.println(loginResponse.getHeaders());
	    assertNotNull(cookies, "Session cookie should be present");
	    String sessionCookie = cookies.get(0);

	    // Now use the session cookie for authentication
	    HttpHeaders authenticatedHeaders = new HttpHeaders();
	    authenticatedHeaders.add(HttpHeaders.COOKIE, sessionCookie);
	    HttpEntity<String> authenticatedEntity = new HttpEntity<>(authenticatedHeaders);

	    // Perform the actual GET request
	    ResponseEntity<String> response = restTemplate.exchange(
	            "http://localhost:" + port + "/api/github/repositories?language=Java&created_after=2020-01-01&page=1",
	            HttpMethod.GET, authenticatedEntity, String.class
	    );

	    // Validate response
	    assertEquals(HttpStatus.OK, response.getStatusCode(), "Authenticated request should return 200 OK");

	    // Validate Redis caching
	    Set<String> allKeys = redisTemplate.keys("*");
	    System.out.println("All Redis Keys: ");
	    allKeys.forEach(System.out::println);

	    String redisKey = "github_repos:Java:2020-01-01";
	    Object result = redisTemplate.opsForValue().get(redisKey);

	    System.out.println("Redis Key: " + redisKey);
	    System.out.println("Redis Value: " + result);

	    assertNotNull(result, "Data from GitHub should be stored in Redis");
	}



}
