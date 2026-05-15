# E-Commerce Marketplace

"E-Commerce Marketplace is a multi-vendor backend API that supports user authentication, role-based access control, shop management, and the foundation for product catalog, inventory, order, payment, and analytics features. It is developed with **Java 21** and **Spring Boot 3**, using **PostgreSQL** as the primary database, **Redis** for caching/session support, and **Meilisearch** for future product search capabilities."

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Environment Variables](#environment-variables)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [API Endpoints](#api-endpoints)
- [Contact](#contact)

## Features

- JWT authentication
- Refresh token flow and token logout blacklist
- Stateless Spring Security configuration
- Standard API response wrapper
- Centralized exception handling
- Dynamic role-based access control with shop-level permissions
- User profile management
- Address book management as a separate API module
- Multi-vendor shop management
- Shop employee assignment and role management
- Custom shop roles and role permission assignment
- Soft delete support for shops
- Docker Compose setup for PostgreSQL, Redis, and Meilisearch
- API documentation with Swagger UI
- Modular layered backend structure based on controller, service, repository, entity, and DTO layers

## Tech Stack

- **Language:** Java 21
- **Backend Framework:** Spring Boot 3.x
- **Build Tool:** Maven
- **Security:** Spring Security, JSON Web Tokens (JWT)
- **Database:** PostgreSQL
- **Caching:** Redis
- **Search Engine:** Meilisearch
- **Persistence:** Spring Data JPA
- **Validation:** Jakarta Validation
- **Boilerplate Reduction:** Lombok
- **API Docs:** Swagger / OpenAPI
- **Containerization:** Docker Compose

## Architecture

The backend follows a modular layered architecture:

```text
com.jo.marketplace
├── common
├── config
├── constant
├── controller
├── entity
├── exception
├── model
│   ├── dto
│   │   ├── request
│   │   └── response
│   └── enums
├── repository
├── security
├── service
│   └── interfaces
├── specification
└── utils
```

Request flow:

```text
HTTP Request
-> Controller
-> Service Interface
-> Service Implementation
-> Repository
-> Database or External Service
-> Response Wrapper
```

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker and Docker Compose
- PostgreSQL instance, if not using Docker Compose
- Redis instance, if not using Docker Compose
- Meilisearch instance, if not using Docker Compose

## Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/Jonathan-dev2002/marketplace.git
   cd marketplace
   ```

2. **Start infrastructure services**

   ```bash
   docker compose up -d
   ```

3. **Navigate to the backend module**

   ```bash
   cd marketplace
   ```

4. **Install dependencies and compile**

   ```bash
   mvn -DskipTests compile
   ```

## Environment Variables

The application provides local defaults in `marketplace/src/main/resources/application.yml`.

You can override them with the following environment variables:

```ini
# Database
DB_HOST=localhost
DB_PORT=5433
DB_NAME=marketplace_db
DB_USERNAME=root
DB_PASSWORD=secretpassword

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=your_base64_encoded_jwt_secret

# Default Admin User
ADMIN_USERNAME=system_admin
ADMIN_EMAIL=admin@marketplace.com
ADMIN_PASSWORD=Admin@1234
ADMIN_FIRST_NAME=System
ADMIN_LAST_NAME=Administrator
```

## Running the Application

1. **Start PostgreSQL, Redis, and Meilisearch**

   ```bash
   docker compose up -d
   ```

2. **Run the Spring Boot application**

   ```bash
   cd marketplace
   mvn spring-boot:run
   ```

3. **Compile the application**

   ```bash
   cd marketplace
   mvn -DskipTests compile
   ```

Default service ports:

- Backend API: `http://localhost:8080`
- PostgreSQL: `localhost:5433`
- Redis: `localhost:6379`
- Meilisearch: `localhost:7700`

## API Documentation

Swagger UI is available after starting the backend:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/api-docs
```

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user.
- `POST /api/auth/login` - Login with username/email and password.
- `POST /api/auth/logout` - Logout and blacklist the current access token.
- `POST /api/auth/refresh` - Rotate a refresh token and issue a new token pair.
- `POST /api/auth/change-password` - Change the current user's password.

### Users

- `GET /api/v1/users/me` - Get the current user's profile.
- `PATCH /api/v1/users/me` - Update the current user's profile.
- `PATCH /api/v1/users/me/deactivate` - Deactivate the current user's account.

### Addresses

- `GET /api/v1/addresses` - List the current user's addresses.
- `POST /api/v1/addresses` - Create an address.
- `GET /api/v1/addresses/{addressId}` - Get an address by ID.
- `PATCH /api/v1/addresses/{addressId}` - Update an address.
- `DELETE /api/v1/addresses/{addressId}` - Soft delete an address.
- `PATCH /api/v1/addresses/{addressId}/default` - Set the default address.

### Shops

- `POST /api/v1/shops` - Create a shop for the current user.
- `GET /api/v1/shops/{shopId}` - Get shop details.
- `GET /api/v1/shops/me` - List shops owned by or assigned to the current user.
- `PATCH /api/v1/shops/{shopId}` - Update shop profile information.
- `PATCH /api/v1/shops/{shopId}/status` - Update shop active status.
- `PATCH /api/v1/shops/{shopId}/slug` - Update shop slug.
- `DELETE /api/v1/shops/{shopId}` - Soft delete a shop.
- `GET /api/v1/shops/{shopId}/employees` - List shop employees.
- `POST /api/v1/shops/{shopId}/employees` - Assign an employee to a shop.
- `DELETE /api/v1/shops/{shopId}/employees/{userId}` - Remove an employee from a shop.
- `PATCH /api/v1/shops/{shopId}/employees/{userId}/role` - Change an employee's shop role.
- `GET /api/v1/shops/{shopId}/roles` - List system and custom roles available for a shop.
- `POST /api/v1/shops/{shopId}/roles` - Create a custom shop role.
- `PATCH /api/v1/shops/{shopId}/roles/{roleId}` - Update a custom shop role.
- `DELETE /api/v1/shops/{shopId}/roles/{roleId}` - Delete a custom shop role.
- `PUT /api/v1/shops/{shopId}/roles/{roleId}/permissions` - Replace role permissions.
- `GET /api/v1/shops/{shopId}/permissions/me` - List the current user's permissions in a shop.

## Contact

For questions or feedback, reach out to:

**Email:**

- [jonathandoillon2002@gmail.com](mailto:jonathandoillon2002@gmail.com)
