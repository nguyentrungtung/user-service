# Quick Start Guide - User Service API Testing

## Overview
This guide provides quick instructions for testing the User Service API using the provided tools and documentation.

## Files Created

1. **`API_DOCUMENTATION.md`** - Complete API documentation with curl examples
2. **`User_Service_API.postman_collection.json`** - Postman collection for all endpoints
3. **`test_api.sh`** - Comprehensive test script for all API endpoints
4. **`QUICK_START_GUIDE.md`** - This guide

## Prerequisites

Before testing the API, ensure you have:

1. **Application Running**: The User Service should be running on `http://localhost:8080`
2. **Database**: PostgreSQL should be running and accessible
3. **Cache**: Redis should be running and accessible
4. **Dependencies**: `curl` and `jq` installed for testing scripts

## Quick Start Options

### Option 1: Using the Test Script (Recommended)

The easiest way to test all endpoints:

```bash
# Run the comprehensive test script
./test_api.sh

# Run additional test scenarios
./additional_test_scenarios.sh
```

This script will:
- Test all API endpoints
- Create and clean up test data
- Show success/failure status for each test
- Display response data in JSON format

### Option 2: Using Postman Collection

1. **Import Collection**: Import `User_Service_API.postman_collection.json` into Postman
2. **Set Variables**: Update the collection variables:
   - `baseUrl`: `http://localhost:8080/api/v1/users`
   - `tenantId`: `tenant1`
   - `domainId`: `domain1`
3. **Test Endpoints**: Run individual requests or the entire collection

### Option 3: Manual Testing with Curl

Use the examples from `API_DOCUMENTATION.md`:

```bash
# Basic health check
curl -X GET http://localhost:8080/actuator/health

# Create a user
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Password123!",
    "firstName": "Test",
    "lastName": "User"
  }'

# Get all users
curl -X GET http://localhost:8080/api/v1/users \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

## Testing Workflow

### 1. Start the Application

```bash
# If using Docker
docker-compose up -d

# If running locally
mvn spring-boot:run
```

### 2. Verify Application is Running

```bash
curl -X GET http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

### 3. Run Tests

Choose one of the testing options above based on your preference.

## Common Test Scenarios

### Complete User Lifecycle Test

```bash
#!/bin/bash

# Set variables
BASE_URL="http://localhost:8080/api/v1/users"
TENANT_ID="tenant1"
DOMAIN_ID="domain1"

# 1. Create user
echo "Creating user..."
RESPONSE=$(curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID" \
  -d '{
    "username": "lifecycle_test",
    "email": "lifecycle@example.com",
    "password": "Password123!",
    "firstName": "Lifecycle",
    "lastName": "Test"
  }')

USER_ID=$(echo $RESPONSE | jq -r '.id')
echo "Created user with ID: $USER_ID"

# 2. Get user
echo "Getting user..."
curl -s -X GET $BASE_URL/$USER_ID \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID" | jq

# 3. Update user
echo "Updating user..."
curl -s -X PUT $BASE_URL/$USER_ID \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID" \
  -d '{
    "firstName": "Updated",
    "isEmailVerified": true
  }' | jq

# 4. Get user permissions
echo "Getting permissions..."
curl -s -X GET $BASE_URL/$USER_ID/permissions \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID" | jq

# 5. Cache permissions
echo "Caching permissions..."
curl -s -X POST $BASE_URL/$USER_ID/permissions/cache \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID"

# 6. Deactivate user
echo "Deactivating user..."
curl -s -X POST $BASE_URL/$USER_ID/deactivate \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID"

# 7. Delete user
echo "Deleting user..."
curl -s -X DELETE $BASE_URL/$USER_ID \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID"

echo "Lifecycle test completed!"
```

### Permission and Role Testing

```bash
#!/bin/bash

# After creating a user (USER_ID must be set)
BASE_URL="http://localhost:8080/api/v1/users"
TENANT_ID="tenant1"
DOMAIN_ID="domain1"

# Test permission checking
echo "Checking permissions..."
curl -s -X GET "$BASE_URL/$USER_ID/permissions/check?permission=user:read" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID"

# Test role checking
echo "Checking roles..."
curl -s -X GET "$BASE_URL/$USER_ID/roles/check?role=USER" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID"
```

## Expected Responses

### Success Response (User Creation)
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "tenantId": "tenant1",
  "resourceDomain": "domain1",
  "username": "testuser",
  "email": "test@example.com",
  "firstName": "Test",
  "lastName": "User",
  "isActive": true,
  "isEmailVerified": false,
  "createdAt": "2025-01-14T10:30:00"
}
```

### Error Response (400 Bad Request)
```json
{
  "timestamp": "2025-01-14T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Username is required",
  "path": "/api/v1/users"
}
```

### Permission Response
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "testuser",
  "tenantId": "tenant1",
  "resourceDomain": "domain1",
  "permissions": ["user:read", "user:write"],
  "roles": ["USER"]
}
```

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Ensure the application is running on port 8080
   - Check if Docker containers are up: `docker-compose ps`

2. **404 Not Found**
   - Verify the endpoint URL is correct
   - Check if the API path starts with `/api/v1/users`

3. **400 Bad Request**
   - Check request headers (X-Tenant-ID, X-Domain-ID)
   - Validate JSON payload format
   - Ensure required fields are provided

4. **500 Internal Server Error**
   - Check application logs
   - Verify database connection
   - Ensure Redis is running

### Debug Tips

1. **Check Application Health**
   ```bash
   curl -X GET http://localhost:8080/actuator/health
   ```

2. **View Application Logs**
   ```bash
   # If using Docker
   docker-compose logs user-service
   
   # If running locally
   tail -f logs/application.log
   ```

3. **Test Database Connection**
   ```bash
   # Check if PostgreSQL is accessible
   curl -X GET http://localhost:8080/actuator/health/db
   ```

4. **Test Redis Connection**
   ```bash
   # Check if Redis is accessible
   curl -X GET http://localhost:8080/actuator/health/redis
   ```

## Next Steps

1. **Production Testing**: Use different tenant and domain IDs to test multi-tenancy
2. **Load Testing**: Use tools like JMeter or k6 for performance testing
3. **Security Testing**: Test authentication and authorization mechanisms
4. **Integration Testing**: Test with other microservices in your ecosystem

## Support

For questions or issues:
1. Check the `API_DOCUMENTATION.md` for detailed endpoint information
2. Review the `IMPLEMENTATION_SUMMARY.md` for architecture details
3. Run the test script to identify specific failing endpoints
4. Check application logs for error details
