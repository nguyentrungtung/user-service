# User Service API Documentation

## Overview
This document provides comprehensive API documentation for the User Service microservice with curl commands and Postman collection examples.

**Base URL**: `http://localhost:8080/api/v1/users`

## Required Headers
All API endpoints require the following headers:
- `X-Tenant-ID`: The tenant identifier
- `X-Domain-ID`: The domain identifier  
- `Content-Type`: `application/json` (for POST/PUT requests)

## API Endpoints

### 1. User Management

#### Create User
Creates a new user in the system.

**Endpoint**: `POST /api/v1/users`

**Curl Command**:
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1" \
  -d '{
    "username": "john.doe",
    "email": "john.doe@example.com",
    "password": "Password123!",
    "firstName": "John",
    "lastName": "Doe",
    "isActive": true
  }'
```

**Response Example**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "tenantId": "tenant1",
  "resourceDomain": "domain1",
  "username": "john.doe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "isActive": true,
  "isEmailVerified": false,
  "lastLoginAt": null,
  "createdAt": "2025-01-14T10:30:00",
  "updatedAt": "2025-01-14T10:30:00",
  "createdBy": "system",
  "updatedBy": "system",
  "roles": [],
  "permissions": []
}
```

#### Get User by ID
Retrieves user information by user ID.

**Endpoint**: `GET /api/v1/users/{userId}`

**Curl Command**:
```bash
curl -X GET http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000 \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

#### Get User by Username
Retrieves user information by username.

**Endpoint**: `GET /api/v1/users/username/{username}`

**Curl Command**:
```bash
curl -X GET http://localhost:8080/api/v1/users/username/john.doe \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

#### Update User
Updates user information.

**Endpoint**: `PUT /api/v1/users/{userId}`

**Curl Command**:
```bash
curl -X PUT http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1" \
  -d '{
    "email": "john.updated@example.com",
    "firstName": "Jonathan",
    "lastName": "Doe",
    "isActive": true,
    "isEmailVerified": true
  }'
```

#### Delete User
Deletes a user from the system.

**Endpoint**: `DELETE /api/v1/users/{userId}`

**Curl Command**:
```bash
curl -X DELETE http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000 \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

#### Get All Users
Retrieves all users in the tenant and domain.

**Endpoint**: `GET /api/v1/users`

**Curl Command**:
```bash
curl -X GET http://localhost:8080/api/v1/users \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

#### Search Users
Searches for users by term (username, email, firstName, lastName).

**Endpoint**: `GET /api/v1/users/search?q={searchTerm}`

**Curl Command**:
```bash
curl -X GET "http://localhost:8080/api/v1/users/search?q=john" \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

### 2. User Status Management

#### Activate User
Activates a user account.

**Endpoint**: `POST /api/v1/users/{userId}/activate`

**Curl Command**:
```bash
curl -X POST http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000/activate \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

#### Deactivate User
Deactivates a user account.

**Endpoint**: `POST /api/v1/users/{userId}/deactivate`

**Curl Command**:
```bash
curl -X POST http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000/deactivate \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

#### Verify Email
Verifies a user's email address.

**Endpoint**: `POST /api/v1/users/{userId}/verify-email`

**Curl Command**:
```bash
curl -X POST http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000/verify-email \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

### 3. Permission Management

#### Get User Permissions
Retrieves all permissions for a user (includes permissions from roles).

**Endpoint**: `GET /api/v1/users/{userId}/permissions`

**Curl Command**:
```bash
curl -X GET http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000/permissions \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

**Response Example**:
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john.doe",
  "tenantId": "tenant1",
  "resourceDomain": "domain1",
  "permissions": [
    "user:read",
    "user:write",
    "role:read"
  ],
  "roles": [
    "USER",
    "EDITOR"
  ]
}
```

#### Cache User Permissions
Caches user permissions in Redis for faster access.

**Endpoint**: `POST /api/v1/users/{userId}/permissions/cache`

**Curl Command**:
```bash
curl -X POST http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000/permissions/cache \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

#### Evict User Permissions Cache
Removes cached permissions from Redis.

**Endpoint**: `DELETE /api/v1/users/{userId}/permissions/cache`

**Curl Command**:
```bash
curl -X DELETE http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000/permissions/cache \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

#### Check User Permission
Checks if a user has a specific permission.

**Endpoint**: `GET /api/v1/users/{userId}/permissions/check?permission={permissionName}`

**Curl Command**:
```bash
curl -X GET "http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000/permissions/check?permission=user:read" \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

**Response Example**:
```json
true
```

#### Assign Permission to User
Assigns a specific permission directly to a user.

**Endpoint**: `POST /api/v1/users/{userId}/permissions/{permissionId}`

**Curl Command**:
```bash
curl -X POST http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000/permissions/650e8400-e29b-41d4-a716-446655440001 \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

#### Remove Permission from User
Removes a specific permission from a user.

**Endpoint**: `DELETE /api/v1/users/{userId}/permissions/{permissionId}`

**Curl Command**:
```bash
curl -X DELETE http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000/permissions/650e8400-e29b-41d4-a716-446655440001 \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

### 4. Role Management

#### Get User Roles
Retrieves all roles assigned to a user.

**Endpoint**: `GET /api/v1/users/{userId}/roles`

**Curl Command**:
```bash
curl -X GET http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000/roles \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

**Response Example**:
```json
[
  {
    "id": "750e8400-e29b-41d4-a716-446655440001",
    "name": "USER",
    "description": "Basic user role",
    "tenantId": "tenant1",
    "resourceDomain": "domain1",
    "permissions": [
      {
        "id": "850e8400-e29b-41d4-a716-446655440001",
        "name": "user:read",
        "description": "Read user data"
      }
    ]
  }
]
```

#### Assign Role to User
Assigns a role to a user.

**Endpoint**: `POST /api/v1/users/{userId}/roles/{roleId}`

**Curl Command**:
```bash
curl -X POST http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000/roles/750e8400-e29b-41d4-a716-446655440001 \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

#### Remove Role from User
Removes a role from a user.

**Endpoint**: `DELETE /api/v1/users/{userId}/roles/{roleId}`

**Curl Command**:
```bash
curl -X DELETE http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000/roles/750e8400-e29b-41d4-a716-446655440001 \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

#### Check User Role
Checks if a user has a specific role.

**Endpoint**: `GET /api/v1/users/{userId}/roles/check?role={roleName}`

**Curl Command**:
```bash
curl -X GET "http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000/roles/check?role=USER" \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

**Response Example**:
```json
true
```

### 5. Validation

#### Check Username Availability
Checks if a username is available in the tenant and domain.

**Endpoint**: `GET /api/v1/users/username/{username}/available`

**Curl Command**:
```bash
curl -X GET http://localhost:8080/api/v1/users/username/john.doe/available \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

**Response Example**:
```json
false
```

#### Check Email Availability
Checks if an email is available in the tenant and domain.

**Endpoint**: `GET /api/v1/users/email/{email}/available`

**Curl Command**:
```bash
curl -X GET http://localhost:8080/api/v1/users/email/john.doe@example.com/available \
  -H "X-Tenant-ID: tenant1" \
  -H "X-Domain-ID: domain1"
```

**Response Example**:
```json
false
```

## Error Responses

### Common Error Formats

#### 400 Bad Request
```json
{
  "timestamp": "2025-01-14T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Username is required",
  "path": "/api/v1/users"
}
```

#### 404 Not Found
```json
{
  "timestamp": "2025-01-14T10:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 550e8400-e29b-41d4-a716-446655440000",
  "path": "/api/v1/users/550e8400-e29b-41d4-a716-446655440000"
}
```

#### 409 Conflict
```json
{
  "timestamp": "2025-01-14T10:30:00.000+00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Username already exists: john.doe",
  "path": "/api/v1/users"
}
```

#### 500 Internal Server Error
```json
{
  "timestamp": "2025-01-14T10:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/v1/users"
}
```

## Health Check Endpoints

### Application Health
```bash
curl -X GET http://localhost:8080/actuator/health
```

### Application Info
```bash
curl -X GET http://localhost:8080/actuator/info
```

### Application Metrics
```bash
curl -X GET http://localhost:8080/actuator/metrics
```

## Testing Scripts

### Complete User Workflow Test
```bash
#!/bin/bash

# Set common variables
BASE_URL="http://localhost:8080/api/v1/users"
TENANT_ID="tenant1"
DOMAIN_ID="domain1"
USER_ID=""

# Create user
echo "Creating user..."
RESPONSE=$(curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Password123!",
    "firstName": "Test",
    "lastName": "User"
  }')

echo "Create response: $RESPONSE"

# Extract user ID (requires jq)
USER_ID=$(echo $RESPONSE | jq -r '.id')
echo "User ID: $USER_ID"

# Get user by ID
echo "Getting user by ID..."
curl -s -X GET $BASE_URL/$USER_ID \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID" | jq

# Get user permissions
echo "Getting user permissions..."
curl -s -X GET $BASE_URL/$USER_ID/permissions \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID" | jq

# Cache permissions
echo "Caching permissions..."
curl -s -X POST $BASE_URL/$USER_ID/permissions/cache \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID"

# Check username availability
echo "Checking username availability..."
curl -s -X GET $BASE_URL/username/testuser/available \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID"

# Update user
echo "Updating user..."
curl -s -X PUT $BASE_URL/$USER_ID \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID" \
  -d '{
    "firstName": "Updated",
    "lastName": "User",
    "isEmailVerified": true
  }' | jq

# Delete user
echo "Deleting user..."
curl -s -X DELETE $BASE_URL/$USER_ID \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "X-Domain-ID: $DOMAIN_ID"

echo "Test completed!"
```

## Notes

1. **Multi-tenancy**: All endpoints require `X-Tenant-ID` and `X-Domain-ID` headers for proper data isolation.

2. **UUIDs**: All user IDs, role IDs, and permission IDs are UUIDs.

3. **Caching**: User permissions are cached in Redis for performance. Use cache endpoints to manage cached data.

4. **Validation**: Username must be 3-50 characters, password must be at least 8 characters, and email must be valid.

5. **Status Codes**: 
   - 200: Success
   - 201: Created
   - 204: No Content (for DELETE operations)
   - 400: Bad Request
   - 404: Not Found
   - 409: Conflict
   - 500: Internal Server Error

6. **Security**: Passwords are hashed using BCrypt before storage.

7. **Database**: Uses PostgreSQL with Flyway migrations for schema management.
