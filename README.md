# Smart Equip - check user

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.2-green.svg)
![Docker](https://img.shields.io/badge/Docker-20.10.21-blue.svg)
![Build](https://github.com/ibotirama/se-challenge/User/actions/workflows/maven.yml/badge.svg)

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Running](#running)
    - [Running Locally](#running-locally)
    - [Using Docker](#using-docker)
- [Running Tests](#running-tests)
- [Tests Coverage](#tests-coverage)
- [API Documentation](#api-documentation)
- [Trade-offs](#trade-offs)
- [Future Enhancements](#future-enhancements)

## Overview

**Check User** is a Java Spring Boot application that provides a simple API to generate sum questions with random numbers and validate the answers submitted by clients. The server is designed to be stateless by leveraging JSON Web Tokens (JWT) to encode question data, ensuring scalability and security without maintaining server-side state.

## Features

- **Generate Sum Questions:** Provides clients with sum questions containing random numbers.
- **Validate Answers:** Accepts answers from clients and verifies their correctness.
- **Stateless Architecture:** Utilizes JWT to maintain session information without server-side storage.
- **Secure:** Ensures that answers are tied to previously issued questions via token validation.
- **Automated Testing:** Comprehensive unit and integration tests to ensure reliability.
- **Containerization:** Dockerfile included for easy deployment and scalability.

## Technologies Used

- **Java 17**
- **Spring Boot 3.1.2**
- **Maven**
- **JWT (JSON Web Tokens)**
- **Docker**
- **JUnit 5 & Mockito** for testing

## Prerequisites

To run **Check User** on a Mac, ensure you have the following installed:

- **Java 17 SDK**
- **Maven**
- **Docker** (optional, for containerization)

## Running
### Running locally
```bash
./mvnw spring-boot:run
```

### Using docker
```bash
docker build -t se-challenge .
docker run -p 8080:8080 se-challenge
```


## Running tests
```bash
./mvnw test
```
## Tests Coverage
```bash
./mvnw verify
```
Then open /target/site/jacoco/index.html to see the coverage report.

## API Documentation

## Trade-offs
 - The application is designed to be stateless, meaning that it does not store any session information on the server. This is achieved by encoding question data into a JSON Web Token (JWT) and sending it to the client. When the client submits an answer, the server decodes the JWT to verify the answer.
 - The application uses a simple in-memory repository to store the questions and answers. In a production environment, this would be replaced with a persistent data store.
 - Docker in two stages to have a smaller image size. The first stage builds the application using Maven, and the second stage copies the JAR file into a new image.
 - The application was not split in two separated services because it is too simple to be separated. In a future stage, it could be split in two services to improve scalability and performance if it can't handle a high demand.
 - On the validation if I wanted to control the exceptions, I could have a custom exception to control if the question is the same encoded in the token, or the answer was not correct. But I opted for simplicity just returning false in this case.
 - Decided to not test the JwtUtil class because it is a simple class and the tests would be redundant.
 - Decided to always return 3 numbers just by simplicity

## Feature enhancements
  - Check if the token is expired - Forgot it initially :)
  - Add different operations to the questions and get it from the question when validating the answer.
  - Add resilience patterns like rate limiting, timeout, etc.

