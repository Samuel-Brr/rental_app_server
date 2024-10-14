# Rental Application

This is a Spring Boot application for managing rental properties. It provides APIs for user authentication, rental management, and messaging.

## Prerequisites

- Java JDK 17 or later
- Maven 3.6 or later
- MySQL 8.0 or later

## Setup

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/rental-app.git
cd rental-app
```

### 2. Database Setup

1. Create a MySQL database:

```sql
CREATE DATABASE rentalapp;
```

2. Create a `application-secret.properties` file with your database credentials:

```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```
3. Update the `application.properties` file with your database url:

```properties
spring.datasource.url=jdbc:mysql://localhost:your-path/rentalapp
```
4. When you run your application for the first time the tables of your database will be automatically generated by spring.
```properties
spring.jpa.hibernate.ddl-auto=update
```

### 3. Configure Application Properties

Update the `application-secret.properties` file with the following configurations:

```properties
jwt.secret.key=yourLongJwtKey
jwt.issuer=issuerOfYourJwt
```
Replace `your_jwt_secret_key` with a secure random string.

### 4. Build the Project

Run the following command in the project root directory:

```bash
mvn clean install
```

## Running the Application

Start the application using:

```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:3001`.

## API Documentation

Once the application is running, you can access the Swagger UI for API documentation at:

```
http://localhost:3001/swagger-ui/index.html
```

## Using the API

1. Register a new user:
   - POST `/api/auth/register`
   - Body: `{ "name": "Your Name", "email": "your@email.com", "password": "yourpassword" }`

2. Login:
   - POST `/api/auth/login`
   - Body: `{ "login": "your@email.com", "password": "yourpassword" }`
   - This will return a JWT token

3. Use the JWT token in the Authorization header for subsequent requests:
   - Header: `Authorization: Bearer your_jwt_token`

4. Create a rental:
   - POST `/api/rentals`
   - Use multipart/form-data to include rental details and picture

5. Get all rentals:
   - GET `/api/rentals`

6. Get a specific rental:
   - GET `/api/rentals/{id}`

7. Update a rental:
   - PUT `/api/rentals/{id}`

8. Send a message:
   - POST `/api/messages`

## Troubleshooting

- If you encounter any issues with file uploads, ensure that the `app.upload.dir` directory exists and has write permissions.
- For database connection issues, verify your MySQL credentials and ensure the server is running.
