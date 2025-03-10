# RepoScorer

RepoScorer is a Spring Boot application that fetches and scores GitHub repositories based on certain criteria, such as stars, forks, watchers, and issues. It uses the GitHub API to fetch repositories based on specified parameters (such as programming language and creation date) and calculates a score for each repository. The results are then cached using Redis to improve performance for subsequent requests.

## Features

- Fetch repositories from GitHub using specific queries (language, creation date).
- Calculate a score for each repository based on various metrics (stars, forks, watchers, issues).
- Cache results in Redis to speed up future requests.
- Rate-limited token pool is used to authenticate requests to GitHub, with token usage tracked in Redis for efficient load distribution.
- Exposes a REST API with Swagger UI for easy testing and interaction.
- Error handling and input validation for language, date, and page parameters to ensure correct and meaningful results.

## Endpoints

You can access the REST API documentation via Swagger at the following URL:

http://localhost:8080/swagger-ui/index.html

- `GET /api/github/repositories`
    - **Parameters:**
      - `language`: Programming language to filter repositories (e.g., `java`, `python`).
      - `created_after`: Filter repositories created after this date (in `YYYY-MM-DD` format).
      - `page`: Page number for paginated results (default is 1).
      
### Example Request

GET http://localhost:8080/api/github/repositories?language=java&created_after=2022-01-01&page=1

### Example Response

```json
{
  "total_count": 40,
  "repositories": [
    {
      "name": "RepoName",
      "fullName": "RepoOwner/RepoName",
      "description": "A description of the repository.",
      "stars": 1500,
      "forks": 300,
      "watchers": 100,
      "issues": 5,
      "updated_at": "2025-02-10T00:00:00Z",
      "score": 85.5
    }
  ]
}
```


## Running the application
To run the application, follow these steps:

1. Download the repository from [https://github.com/saquibw/RepoScorer](https://github.com/saquibw/RepoScorer).

2. The `.env` file containing the GitHub API token pool can be downloaded from the following link:  
   [https://drive.google.com/file/d/1Yy4APvOIeBnHqYtOR0fNd7wmdy1bh3VT/view?usp=sharing](#).
   
3. Place the downloaded `.env` file in the **root directory** of the project.

4. Build the Docker images and start the services using the command:
   
```bash
docker compose up --build
```

This command will build the Docker images for both the app and the Redis server, then start both containers.

The app will be available at http://localhost:8080.

Redis will run on its default port 6379.

You can access the Swagger UI at http://localhost:8080/swagger-ui/index.html to explore and test the API.

Note: GitHub tokens have a validity of 30 days (starting from February 19, 2025)


## Redis Setup
The application uses Redis for caching repository data. If you're running without Docker, you need to ensure that Redis is running locally or modify the configuration to connect to a remote Redis instance.

## Testing the API
You can test the API via the Swagger UI at:

http://localhost:8080/swagger-ui/index.html

## Authentication
The application is protected with basic authentication. Default credential is:

User name: user

Password: password

## Token Pool Implementation
The system uses a token pool to authenticate requests to GitHub, with each token allowing 30 requests per minute. Every minute, the token's usage counter is reset by GitHub. We store these counters in Redis to track the usage of each token. When making an API request, the system picks a token from the pool based on its current counter.