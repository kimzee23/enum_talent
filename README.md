# telant_auth
# EnumTalent API 

A Spring Boot REST API for talent management and recruitment platform with authentication, profile management, and OAuth2 integration.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Environment Setup](#environment-setup)
- [Development](#development)
- [Deployment](#deployment)
- [Testing](#testing)
- [Contributing](#contributing)

## Features

### Authentication & Authorization
- **Email/Password Signup & Login** with email verification
- **OAuth2 Integration** (Google & Apple) - Ready for implementation
- **JWT-based stateless authentication**
- **Secure password hashing** with BCrypt

### User Management
- **User registration** with email verification
- **Profile completeness tracking**
- **Role-based access control** (Ready for implementation)

### Talent Profiles
- **Comprehensive profile management**
- **Profile completeness scoring**
- **Academic transcript & statement of purpose storage**
- **Skills and experience tracking**

### Security
- **CSRF protection** (configurable)
- **CORS configuration**
- **Input validation and sanitization**
- **Custom exception handling**

## Tech Stack

### Backend
- **Java 24**
- **Spring Boot 3.2.x**
- **Spring Security** with OAuth2
- **Spring Data MongoDB**
- **Maven** for dependency management

### Database
- **MongoDB** - NoSQL database
- **MongoDB Compass** - GUI for database management

### Tools & Utilities
- **Lombok** - Reduced boilerplate code
- **Spring Boot DevTools** - Hot reload for development
- **Spring Boot Validation** - Input validation

## Project Structure

```
enumtalent-api/
├── src/
│   └── main/
│       └── java/
│           └── org/example/enumtalentapi/
│               ├── config/           # Configuration classes
│               ├── controller/       # REST controllers
│               ├── service/          # Business logic
│               ├── repository/       # Data access layer
│               ├── entity/           # MongoDB entities
│               ├── dto/              # Data transfer objects
│               └── exception/        # Custom exceptions
├── src/test/java/                    # Unit and integration tests
├── application.properties            # Main configuration
└── pom.xml                          # Maven dependencies
```

## Quick Start

### Prerequisites

- **Java 17** or higher
- **Maven 3.6** or higher
- **MongoDB** (local or cloud instance)
- **Git**

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/enumtalent-api.git
   cd enumtalent-api
   ```

2. **Configure environment variables**
   ```bash
   # Create .env file
   cp .env.example .env
   
   # Edit with your values
   nano .env
   ```

3. **Set up MongoDB**
   ```bash
   # Using local MongoDB
   mongod --dbpath /path/to/your/db
   
   # Or using Docker
   docker run -d -p 27017:27017 --name mongodb mongo:latest
   ```

4. **Run the application**
   ```bash
   # Using Maven
   mvn spring-boot:run
   
   # Or build and run
   mvn clean package
   java -jar target/enumtalent-api-0.0.1-SNAPSHOT.jar
   ```

The API will be available at `http://localhost:8081`

##  Environment Setup

### Required Environment Variables

Create a `.env` file in the root directory:

```bash
# JWT Configuration
JWT_SECRET=your-super-secure-jwt-secret-key-here

# OAuth2 Configuration (Optional)
GOOGLE_CLIENT_ID=your-google-oauth2-client-id
GOOGLE_CLIENT_SECRET=your-google-oauth2-client-secret

# Database
MONGODB_URI=mongodb://localhost:27017/enumtalent

# Email (Optional - for email verification)
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
```

### Configuration Files

- `application.properties` - Main configuration (safe for git)
- `.env` - Environment variables (DO NOT COMMIT)
- `application-dev.properties` - Development overrides

## API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Body |
|--------|----------|-------------|------|
| `POST` | `/api/auth/signup` | User registration | `{email, password}` |
| `POST` | `/api/auth/login` | User login | `{email, password}` |
| `GET` | `/api/auth/verify?token={token}` | Verify email | - |
| `POST` | `/api/auth/logout` | User logout | `userId` param |

### Profile Endpoints

| Method | Endpoint | Description | Body |
|--------|----------|-------------|------|
| `GET` | `/api/profile/talent/me?userId={id}` | Get user profile | - |
| `POST` | `/api/profile/talent/{userId}` | Create/update profile | Profile data |

### Health Check

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/.well-known/health` | API health status |

### Example Requests

**User Signup:**
```bash
curl -X POST http://localhost:8081/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "securePassword123"}'
```

**Get Profile:**
```bash
curl -X GET "http://localhost:8081/api/profile/talent/me?userId=USER_ID"
```

## Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthControllerTest

# Run with coverage report
mvn test jacoco:report
```

### Test Coverage

- **Unit tests** for services and controllers
- **Integration tests** for API endpoints
- **Mock MVC tests** for HTTP layer

### Manual Testing with HTTP Client

Use the provided `http-requests` files or tools like:
- **Postman**
- **Insomnia**
- **curl** commands

## Deployment

### Local Development
```bash
mvn spring-boot:run
```

### Production Build
```bash
mvn clean package -Pprod
java -jar target/enumtalent-api-0.0.1-SNAPSHOT.jar
```

### Docker Deployment
```dockerfile
# Build image
docker build -t enumtalent-api .

# Run container
docker run -d -p 8081:8081 \
  -e JWT_SECRET=your-secret \
  -e MONGODB_URI=your-connection-string \
  enumtalent-api
```

## Security Considerations

-  Passwords are hashed using BCrypt
-  CSRF protection enabled
-  CORS configured for frontend integration
-  Input validation on all endpoints
-  Custom exception handling
-  Environment-based configuration

## Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add some amazing feature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Development Guidelines

- Write tests for new features
- Follow Java naming conventions
- Use meaningful commit messages
- Update documentation accordingly
- Ensure all tests pass before submitting PR

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

If you encounter any issues:

1. Check the [Troubleshooting](#troubleshooting) section
2. Search existing [Issues](../../issues)
3. Create a new issue with detailed description

### Common Issues

**Port already in use:**
```bash
# Kill process on port 8081
sudo lsof -t -i tcp:8081 | xargs kill -9
```

**MongoDB connection failed:**
- Ensure MongoDB is running
- Check connection string in configuration
- Verify network connectivity

**JWT secret not set:**
- Set `JWT_SECRET` environment variable
- Ensure `.env` file is in root directory

##  Contact

- **Developer**: Kolajo
- **Email**: gazartechnology@gmail.com
- **Project Link**: [https://github.com/kimzee23/enum_talent](https://github.com/kimzee23/enum_talent)

---

** Don't forget to star this repository if you find it helpful!**

---

<div align="center">

**Built with using Spring Boot & MongoDB**

</div>


