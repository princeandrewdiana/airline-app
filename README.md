# ✈ Airline Information System

## 📌 Project Description

A Spring Boot web application that integrates with the **AirLabs API** to display real-time flight, airport, and airline data.  
Built using Spring Boot, Thymeleaf, and Docker with external API integrations for live aviation data.

---

## 🚀 Features

### ✈ Flights
- Search real-time flights by departure airport (IATA code)
- Displays:
  - Flight Number
  - Airline Name
  - Departure & Arrival Airports
  - Flight Status
  - Departure & Arrival Time (enhanced via Schedules API)

### 🏢 Airports
- Browse airport directory with:
  - Airport Name
  - IATA Code
  - City *(limited availability from API)*
  - Country
  - Latitude & Longitude
- Filtering:
  - By Country
  - By City (dynamic dependent dropdown)

### 🛫 Airlines
- View airline directory including:
  - Airline Name
  - IATA Code
  - ICAO Code
  - Country *(limited availability from API)*
  - Status *(limited availability from API)*
    
---

## ⚙️ How to Run Locally

### 1. Clone Repository

```
git clone https://github.com/your-username/airline-app.git
cd airline-app
```
### 2. Configure API Key

#### Choose only one properties file to update:

#### 2.1 `application.properties`
```
airlabs.api.key=${AIRLABS_API_KEY:<YOUR_AIRLABS_API_KEY>}
```

OR 

#### 2.2 `application-local.properties`
```
AIRLABS_API_KEY=<YOUR_AIRLABS_API_KEY>
```
### 3. Run Application
#### 3.1 If you used `application.properties` to configure API key:
```
mvn spring-boot:run
```
#### 3.2 If you used `application-local.properties` to configure API key:
```
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Access:
```
http://localhost:8080
```
---

## 🐳 How to Run with Docker 

### 1. Build Docker Image

Run the following command in the project root directory (where the Dockerfile is located):

```
docker build -t airline-app .
```

### 2. Run Docker Container

Replace `YOUR_AIRLABS_API_KEY` with your actual AirLabs API key.

```
docker run -p 8080:8080 -e AIRLABS_API_KEY=YOUR_AIRLABS_API_KEY airline-app
```

### 3. Open Application

After the container starts successfully, open your browser:

```
http://localhost:8080
```

---

## 🖼️ Screenshots

### Home Page
![Home](screenshots/home.png)

### Flights Page
![Flights](screenshots/flights_init.png)

![Flights](screenshots/flights_search.png)

### Airports Page
![Airports](screenshots/airports_init.png)

![Airports](screenshots/airports_filter.png)

### Airlines Page
![Airlines](screenshots/airlines.png)

---

## 🐳 Docker Hub Image URL
https://hub.docker.com/r/princedianadev/airline-app

![Docker_Hub_Image_URL](screenshots/docker_hub_image_url_v2.png)
