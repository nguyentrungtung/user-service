# User Service Microservice

A comprehensive User Service microservice built with **Java 21**, **Spring Boot 3.x**, and **layered architecture**, featuring multi-tenant support, RBAC (Role-Based Access Control), and Redis caching.

## 🏗️ Architecture Overview

This microservice follows a **layered architecture** pattern with clear separation of concerns:

```
service.com.service.userservice/
├── controller/          # Presentation Layer - REST Controllers
├── service/            # Business Logic Layer - Services & Interfaces
│   └── impl/          # Service Implementations
├── repository/         # Data Access Layer - JPA Repositories
├── entity/            # Entity Layer - JPA Entities
├── dto/               # Data Transfer Objects
├── config/            # Configuration Layer
├── exception/         # Exception Handling
└── utils/             # Utility Classes
```

## 🚀 Key Features

### ✅ Multi-Tenant & Multi-Domain Support
- **Tenant Isolation**: Complete data isolation between tenants
- **Domain Separation**: Resource domain-based access control
- **Headers**: `X-Tenant-ID` and `X-Domain-ID` for context

### ✅ Role-Based Access Control (RBAC)
- **Users**: Identity management with multi-tenant support
- **Roles**: Hierarchical role management
- **Permissions**: Fine-grained permission system
- **Caching**: Redis-based permission caching for performance

### ✅ Redis Integration
- **Permission Caching**: `perm:{tenantId}:{domain}:{userId}`
- **JWT Blacklisting**: `blacklist:{jti}` with TTL
- **Session Management**: User session tracking
- **Cache Eviction**: Automatic cache invalidation

### ✅ Database Features
- **PostgreSQL 15**: Primary database with Flyway migrations
- **Multi-tenant Schema**: Tenant and domain isolation
- **Relationships**: JPA entities with proper associations
- **Indexing**: Optimized queries with strategic indexes

## 🛠️ Technology Stack

- **Backend**: Java 21, Spring Boot 3.2.0
- **Database**: PostgreSQL 15 with Flyway migrations
- **Cache**: Redis 7.2 with Jedis client
- **Security**: Spring Security with BCrypt
- **Documentation**: Spring Boot Actuator
- **Testing**: JUnit 5, Mockito, TestContainers
- **Build**: Maven with layered Docker builds

## 📋 API Endpoints

### User Management
```http
POST   /api/v1/users                    # Create user
GET    /api/v1/users/{userId}           # Get user by ID
GET    /api/v1/users/username/{username} # Get user by username
PUT    /api/v1/users/{userId}           # Update user
DELETE /api/v1/users/{userId}           # Delete user
GET    /api/v1/users                    # List all users
GET    /api/v1/users/search?q={term}    # Search users
```

### User Status Management
```http
POST   /api/v1/users/{userId}/activate     # Activate user
POST   /api/v1/users/{userId}/deactivate   # Deactivate user
POST   /api/v1/users/{userId}/verify-email # Verify email
```

### Permission Management
```http
GET    /api/v1/users/{userId}/permissions              # Get user permissions
POST   /api/v1/users/{userId}/permissions/cache        # Cache permissions
DELETE /api/v1/users/{userId}/permissions/cache        # Evict cache
GET    /api/v1/users/{userId}/permissions/check?permission={perm} # Check permission
```

### Role Management
```http
GET    /api/v1/users/{userId}/roles         # Get user roles
POST   /api/v1/users/{userId}/roles/{roleId} # Assign role
DELETE /api/v1/users/{userId}/roles/{roleId} # Remove role
GET    /api/v1/users/{userId}/roles/check?role={role} # Check role
```

### Validation
```http
GET    /api/v1/users/username/{username}/available # Check username availability
GET    /api/v1/users/email/{email}/available       # Check email availability
```

## 🔧 Configuration

### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://postgresdb:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres

# Redis Configuration
spring.data.redis.host=redis
spring.data.redis.port=6379

# Cache Configuration
app.cache.permissions.ttl=3600    # 1 hour
app.cache.blacklist.ttl=86400     # 24 hours

# Multi-tenant Configuration
app.tenant.header=X-Tenant-ID
app.domain.header=X-Domain-ID
```

### Required Headers
All API requests must include:
```http
X-Tenant-ID: tenant1
X-Domain-ID: domain1
```

## 🗄️ Database Schema

### Core Tables
- **users**: User management with tenant/domain isolation
- **roles**: Role definitions with hierarchical support
- **permissions**: Fine-grained permission system
- **user_roles**: Many-to-many user-role relationships
- **role_permissions**: Many-to-many role-permission relationships
- **user_permissions**: Direct user-permission assignments

### Sample Data
The migration includes sample data for testing:
- **Tenants**: `tenant1`, `tenant2`
- **Domains**: `domain1`, `domain2`
- **Users**: `admin`, `user1` with different roles
- **Permissions**: CRUD operations for users, roles, permissions

## 🐳 Docker Setup

### Development Environment
```bash
# Start services
docker-compose -f .devcontainer/docker-compose.yml up -d

# Access services
- User Service: http://localhost:8080
- pgAdmin: http://localhost:80 (admin@admin.com / admin)
- PostgreSQL: localhost:5432
- Redis: localhost:6379
```

### Production Build
```bash
# Build image
docker build -t user-service:latest .

# Run with environment variables
docker run -e POSTGRES_HOSTNAME=db -e REDIS_HOSTNAME=redis user-service:latest
```

## 🧪 Testing

### Unit Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceImplTest

# Generate test report
mvn test jacoco:report
```

### Integration Tests
```bash
# Run integration tests with TestContainers
mvn test -Dtest=UserServiceIntegrationTest

# Test with actual database
mvn test -Dspring.profiles.active=test
```

## 📊 Monitoring & Health

### Actuator Endpoints
```http
GET /actuator/health        # Health check
GET /actuator/info          # Application info
GET /actuator/metrics       # Application metrics
GET /actuator/env           # Environment properties
```

### Logging
```properties
logging.level.service.com.service.userservice=DEBUG
logging.level.org.springframework.security=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

## 🔐 Security Features

### Password Security
- **BCrypt**: Password hashing with salt
- **Validation**: Strong password requirements
- **Encryption**: Secure password storage

### Session Management
- **Stateless**: JWT-based authentication
- **Blacklisting**: Redis-based JWT blacklisting
- **TTL**: Configurable token expiration

### Multi-tenant Security
- **Isolation**: Complete tenant data isolation
- **Headers**: Required tenant/domain headers
- **Validation**: Tenant context validation

## 🚦 Getting Started

### Prerequisites
- Java 21+
- Docker & Docker Compose
- Maven 3.8+

### Quick Start
1. **Clone repository**
   ```bash
   git clone <repository-url>
   cd user-service
   ```

2. **Start services**
   ```bash
   docker-compose -f .devcontainer/docker-compose.yml up -d
   ```

3. **Build application**
   ```bash
   mvn clean package
   ```

4. **Run application**
   ```bash
   java -jar target/user-service-0.0.1-SNAPSHOT.jar
   ```

5. **Test API**
   ```bash
   curl -X GET http://localhost:8080/actuator/health
   ```

### Sample API Calls
```bash
# Create user
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "Password123!",
    "firstName": "New",
    "lastName": "User"
  }'

# Get user permissions
curl -X GET http://localhost:8080/api/v1/users/{userId}/permissions \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"

# Check permission
curl -X GET http://localhost:8080/api/v1/users/{userId}/permissions/check?permission=user:read \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

## 🔄 CI/CD Pipeline

The project includes a complete GitHub Actions workflow:
- **Testing**: Unit and integration tests
- **Building**: Maven build with dependency caching
- **Docker**: Multi-stage Docker builds
- **Security**: Trivy vulnerability scanning
- **Deployment**: Production-ready deployment pipeline

## 📈 Performance Optimization

### Redis Caching
- **Permission Caching**: 1-hour TTL for user permissions
- **Session Caching**: 24-hour TTL for user sessions
- **Blacklist Caching**: JWT blacklisting with token expiration

### Database Optimization
- **Indexing**: Strategic indexes on frequently queried columns
- **Connection Pooling**: Optimized database connections
- **Query Optimization**: Efficient JPA queries with JOIN FETCH

### JVM Optimization
- **Container Support**: Container-aware JVM settings
- **Memory Management**: Optimized heap size configuration
- **GC Tuning**: Proper garbage collection configuration

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- **Issues**: GitHub Issues
- **Documentation**: See `/docs` directory
- **API Documentation**: `/actuator/info` endpoint

---

**Built with ❤️ using Java 21, Spring Boot 3.x, and layered architecture principles**
