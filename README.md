# Airline Information System

This application is a Spring Boot web app containerized using Docker.  
It integrates with the real-world AirLabs REST API (https://airlabs.co/) to fetch live aviation data and display it in a browser

---

## How to Run with Docker

### 1. Build Docker Image

Run the following command in the project root directory (where the Dockerfile is located):

`docker build -t airline-app .`

---

### 2. Run Docker Container

Replace YOUR_AIRLABS_API_KEY with your actual AirLabs API key.

`docker run -p 8080:8080 -e AIRLABS_API_KEY=YOUR_AIRLABS_API_KEY airline-app`

---

### 3. Open Application

After the container starts successfully, open your browser:

`http://localhost:8080`
