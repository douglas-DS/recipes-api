# Recipes API

A Spring Boot application built with Kotlin that provides RESTful endpoints for managing shopping carts and recipes. 

This service leverages Spring WebFlux for reactive support, Spring Data JPA for persistence, and includes 
out-of-the-box OpenAPI 3.0 and Swagger UI for API documentation.

---

## Table of Contents

1. [Overview](#overview)  
2. [Features](#features)  
3. [Technology Stack](#technology-stack)  
4. [Requirements](#requirements)  
5. [Architecture](#architecture)  
6. [Getting Started](#getting-started)
7. [API Usage](#api-usage)  
   1. [Endpoints](#endpoints)  
   2. [Request/Response Examples](#requestresponse-examples)  
8. [Documentation](#documentation)  
9. [Unit Tests](#unit-tests)

---

## Overview

The **Recipes API** is responsible for creating and retrieving recipes along with their ingredients. 
It showcases database interaction via **Spring Data JPA**.

---

## Features

- **Retrieve all recipes**, including their associated ingredients.
- **Retrieve carts by ID** including their associated items.  
- **Adds a recipe** to a cart.  
- **Removes a recipe** from a cart.  
- **Reactive programming** capabilities with Spring WebFlux.  
- **Data persistence** with Spring Data JPA.  
- **API documentation** automatically generated using OpenAPI 3.0 & served via Swagger UI.

---

## Technology Stack

- **Kotlin**: Primary language for the application.  
- **Spring Boot**: Framework for auto-configuration and packaging.  
  - *Spring WebFlux* for reactive endpoints.  
  - *Spring Data JPA* for data persistence.  
- **H2 Database**: In-memory database for persistence.
- **OpenAPI 3.0 / Swagger UI**: REST API documentation.  
- **Gradle**: Build tool.  
- **Java 21**: Target JVM version.

---

## Requirements

- **Java 21**
- **Gradle** (or use the Gradle Wrapper included in the project).  
---

## Architecture
```

 ┌───────────────────────────┐
 │           Client          │
 └────────────┬──────────────┘
              │ HTTP
              ▼
 ┌───────────────────────────┐
 │  Recipes API (WebFlux)    │
 │  - Controller layer       │
 │  - Service layer          │
 │  - Repository layer       │
 └────────────┬──────────────┘
              │ JPA
              ▼
        (Database Layer)
```
- **Controller** handles HTTP requests.  
- **Service** orchestrates logic (creating recipes, retrieving recipes, etc.).  
- **Repository** interacts with the database using JPA.  
- **Database** is configured as in-memory H2 to simplify setup.

---

## Getting Started

The application can be simply started by running the following command:
```bash
  ./gradlew bootRun
```
> On Windows, use `gradlew.bat` instead of `./gradlew`.

Will be available on [http://localhost:8080](http://localhost:8080) by default.

---

## API Usage

### Endpoints

| Endpoint                         | Method | Description                           |
|----------------------------------|--------|---------------------------------------|
| `/recipes`                       | GET    | Get all recipes paginated             |
| `/carts/{id}`                    | GET    | Get a cart by its ID                  |
| `/carts/{id}/add_recipe`         | POST   | Associate a recipe to a cart          |
| `/carts/{id}/recipes/{recipeId}` | DEL    | Removes a specific recipe from a cart |

### Request/Response Examples

**1. Get all recipes**

- **Request**  
  `GET /recipes`

- **Response** (example):
  ```json
  {
    "content": [
      {
        "id": 1,
        "name": "Chocolate Chip Cookies",
        "description": "Classic homemade chocolate chip cookies",
        "ingredients": [
          { "id": 1, "name": "flour", "quantity": "2 cups" },
          { "id": 2, "name": "chocolate chips", "quantity": "1 cup" },
          { "id": 3, "name": "butter", "quantity": "1/2 cup" }
        ],
        "instructions": "Mix ingredients and bake at 375°F for 10-12 minutes",
        "createdAt": "2025-01-20T18:36:03.186702465Z"
      }
    ]
  }
  ```

**2. Get an existing cart**

- **Request**  
  `GET /carts/{id}`  

- **Response** (example):
  ```json
  {
      "id": 1,
      "total": 4730,
      "items": [
          {
              "id": 1,
              "name": "Classic Chocolate Chip Cookies",
              "kind": "RECIPE",
              "total": 3520
          },
          {
              "id": 2,
              "name": "Whole Milk 1L",
              "kind": "PRODUCT",
              "total": 320
          },
          {
              "id": 3,
              "name": "Vanilla Extract 50ml",
              "kind": "PRODUCT",
              "total": 890
          }
      ]
  }
  ```
---

## Documentation

This application provides automatic API documentation with OpenAPI 3.0 and Swagger UI.

- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)  
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  

When the application is running, simply open the Swagger UI link in your browser to explore and interact with the endpoints.

---

## Unit and Integration Tests

The project includes unit and integration tests for:

- **`RecipeService`**
- **`CartService`**
- **`RecipeController`**
- **`CartController`**

These tests are run by:
```bash
  ./gradlew test
```

They cover:
- **Validation** logic (ensuring requests are valid).  
- **Error handling** (e.g., when a cart/recipe is not found).  
- **Core functionality** (adding/deleting recipe, retrieving recipes and carts).

---