# User Service API Testing - Complete Package

## Summary

I've created a comprehensive API testing package for your User Service with the following components:

## 📁 Created Files

### 1. **API_DOCUMENTATION.md**
- Complete API documentation with all endpoints
- Detailed curl command examples
- Request/response examples  
- Error handling documentation
- Multi-tenant header requirements

### 2. **User_Service_API.postman_collection.json**
- Complete Postman collection with all endpoints
- Pre-configured variables for easy testing
- Organized into logical folders:
  - User Management
  - User Status Management  
  - Permission Management
  - Role Management
  - Validation
  - Health Check

### 3. **test_api.sh**
- Comprehensive bash script for automated testing
- Tests all API endpoints with various scenarios
- Includes error scenario testing
- Automated test data creation and cleanup
- Color-coded output for easy reading

### 4. **QUICK_START_GUIDE.md**
- Step-by-step instructions for testing
- Multiple testing approach options
- Troubleshooting guide
- Common test scenarios
- Expected response examples

## 🔧 API Endpoints Documented

Based on the `UserController.java` analysis, here are all the endpoints:

### User Management (7 endpoints)
- `POST /api/v1/users` - Create user
- `GET /api/v1/users/{userId}` - Get user by ID
- `GET /api/v1/users/username/{username}` - Get user by username
- `PUT /api/v1/users/{userId}` - Update user
- `DELETE /api/v1/users/{userId}` - Delete user
- `GET /api/v1/users` - Get all users
- `GET /api/v1/users/search?q={term}` - Search users

### User Status Management (3 endpoints)
- `POST /api/v1/users/{userId}/activate` - Activate user
- `POST /api/v1/users/{userId}/deactivate` - Deactivate user
- `POST /api/v1/users/{userId}/verify-email` - Verify email

### Permission Management (6 endpoints)
- `GET /api/v1/users/{userId}/permissions` - Get user permissions
- `POST /api/v1/users/{userId}/permissions/cache` - Cache permissions
- `DELETE /api/v1/users/{userId}/permissions/cache` - Evict cache
- `GET /api/v1/users/{userId}/permissions/check?permission={perm}` - Check permission
- `POST /api/v1/users/{userId}/permissions/{permissionId}` - Assign permission
- `DELETE /api/v1/users/{userId}/permissions/{permissionId}` - Remove permission

### Role Management (4 endpoints)
- `GET /api/v1/users/{userId}/roles` - Get user roles
- `POST /api/v1/users/{userId}/roles/{roleId}` - Assign role
- `DELETE /api/v1/users/{userId}/roles/{roleId}` - Remove role
- `GET /api/v1/users/{userId}/roles/check?role={role}` - Check role

### Validation (2 endpoints)
- `GET /api/v1/users/username/{username}/available` - Check username availability
- `GET /api/v1/users/email/{email}/available` - Check email availability

### Health Check (3 endpoints)
- `GET /actuator/health` - Application health
- `GET /actuator/info` - Application info
- `GET /actuator/metrics` - Application metrics

## 🏃 Quick Start

### Option 1: Run Test Script
```bash
./test_api.sh
```

### Option 2: Import Postman Collection
1. Open Postman
2. Import `User_Service_API.postman_collection.json`
3. Update variables as needed
4. Run tests

### Option 3: Manual Testing
```bash
# Health check
curl -X GET http://localhost:8080/actuator/health

# Create user (example)
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
```

## 🔑 Important Notes

### Required Headers
All API endpoints require:
- `X-Tenant-ID`: Tenant identifier (e.g., "tenant1")
- `X-Domain-ID`: Domain identifier (e.g., "domain1")
- `Content-Type: application/json` (for POST/PUT requests)

### Service Status
✅ **Service is currently running** on `http://localhost:8080`

### Authentication Note
The service appears to have security enabled (403 response observed). You may need to:
1. Configure authentication if required
2. Check Spring Security configuration
3. Ensure proper tenant/domain setup in database

### Database Dependencies
- PostgreSQL database must be running
- Redis cache must be running
- Flyway migrations should be applied

## 📊 Test Coverage

The test package covers:
- ✅ All CRUD operations
- ✅ User status management
- ✅ Permission and role management
- ✅ Validation endpoints
- ✅ Health checks
- ✅ Error scenarios
- ✅ Multi-tenant testing
- ✅ Response format validation

## 🎯 Next Steps

1. **Run the test script** to verify all endpoints
2. **Import Postman collection** for interactive testing
3. **Review API documentation** for detailed endpoint information
4. **Check security configuration** if getting 403 errors
5. **Verify database setup** for proper multi-tenant data

## 📝 Files Ready for Use

All files are ready to use immediately:
- Documentation files are in Markdown format
- Postman collection is in standard JSON format
- Test script is executable and ready to run
- All examples use realistic data and proper headers

The package provides everything needed to test and document your User Service API comprehensively!
