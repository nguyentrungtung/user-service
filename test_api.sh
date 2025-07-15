#!/bin/bash

# User Service API Test Script
# This script tests all API endpoints with comprehensive scenarios

set -e  # Exit on error

# Configuration
BASE_URL="http://localhost:8080/api/v1/users"
ACTUATOR_URL="http://localhost:8080/actuator"
TENANT_ID="tenant1"
DOMAIN_ID="domain1"
USER_ID=""
TEST_USERNAME="testuser_$(date +%s)"
TEST_EMAIL="test_$(date +%s)@example.com"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Helper functions
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

print_separator() {
    echo -e "${YELLOW}================================================${NC}"
}

# Test function
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local expected_status=$4
    local description=$5
    
    print_info "Testing: $description"
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "%{http_code}" -X "$method" "$endpoint" \
            -H "Content-Type: application/json" \
            -H "X-Tenant-ID: $TENANT_ID" \
            -H "X-Domain-ID: $DOMAIN_ID" \
            -d "$data")
    else
        response=$(curl -s -w "%{http_code}" -X "$method" "$endpoint" \
            -H "X-Tenant-ID: $TENANT_ID" \
            -H "X-Domain-ID: $DOMAIN_ID")
    fi
    
    # Extract status code (last 3 characters)
    status_code="${response: -3}"
    response_body="${response%???}"
    
    if [ "$status_code" -eq "$expected_status" ]; then
        print_success "$description - Status: $status_code"
        echo "$response_body" | jq . 2>/dev/null || echo "$response_body"
    else
        print_error "$description - Expected: $expected_status, Got: $status_code"
        echo "$response_body"
    fi
    
    echo ""
}

# Health check function
test_health_endpoint() {
    local endpoint=$1
    local description=$2
    
    print_info "Testing: $description"
    
    response=$(curl -s -w "%{http_code}" -X GET "$endpoint")
    status_code="${response: -3}"
    response_body="${response%???}"
    
    if [ "$status_code" -eq 200 ]; then
        print_success "$description - Status: $status_code"
        echo "$response_body" | jq . 2>/dev/null || echo "$response_body"
    else
        print_error "$description - Expected: 200, Got: $status_code"
        echo "$response_body"
    fi
    
    echo ""
}

# Start testing
print_separator
print_info "Starting User Service API Tests"
print_separator

# Test 1: Health Check Endpoints
echo "=== HEALTH CHECK ENDPOINTS ==="
test_health_endpoint "$ACTUATOR_URL/health" "Application Health Check"
test_health_endpoint "$ACTUATOR_URL/info" "Application Info"
test_health_endpoint "$ACTUATOR_URL/metrics" "Application Metrics"

print_separator

# Test 2: User Management
echo "=== USER MANAGEMENT ==="

# Create User
print_info "Creating test user..."
create_user_data='{
    "username": "'$TEST_USERNAME'",
    "email": "'$TEST_EMAIL'",
    "password": "Password123!",
    "firstName": "Test",
    "lastName": "User",
    "isActive": true
}'

response=$(curl -s -w "%{http_code}" -X POST "$BASE_URL" \
    -H "Content-Type: application/json" \
    -H "X-Tenant-ID: $TENANT_ID" \
    -H "X-Domain-ID: $DOMAIN_ID" \
    -d "$create_user_data")

status_code="${response: -3}"
response_body="${response%???}"

if [ "$status_code" -eq 201 ]; then
    print_success "Create User - Status: $status_code"
    USER_ID=$(echo "$response_body" | jq -r '.id')
    print_info "Created User ID: $USER_ID"
    echo "$response_body" | jq .
else
    print_error "Create User - Expected: 201, Got: $status_code"
    echo "$response_body"
    exit 1
fi

echo ""

# Get User by ID
test_endpoint "GET" "$BASE_URL/$USER_ID" "" 200 "Get User by ID"

# Get User by Username
test_endpoint "GET" "$BASE_URL/username/$TEST_USERNAME" "" 200 "Get User by Username"

# Update User
update_user_data='{
    "firstName": "Updated",
    "lastName": "User",
    "isEmailVerified": true
}'
test_endpoint "PUT" "$BASE_URL/$USER_ID" "$update_user_data" 200 "Update User"

# Get All Users
test_endpoint "GET" "$BASE_URL" "" 200 "Get All Users"

# Search Users
test_endpoint "GET" "$BASE_URL/search?q=Updated" "" 200 "Search Users"

print_separator

# Test 3: User Status Management
echo "=== USER STATUS MANAGEMENT ==="

# Activate User
test_endpoint "POST" "$BASE_URL/$USER_ID/activate" "" 200 "Activate User"

# Deactivate User
test_endpoint "POST" "$BASE_URL/$USER_ID/deactivate" "" 200 "Deactivate User"

# Verify Email
test_endpoint "POST" "$BASE_URL/$USER_ID/verify-email" "" 200 "Verify Email"

# Reactivate for further tests
test_endpoint "POST" "$BASE_URL/$USER_ID/activate" "" 200 "Reactivate User"

print_separator

# Test 4: Permission Management
echo "=== PERMISSION MANAGEMENT ==="

# Get User Permissions
test_endpoint "GET" "$BASE_URL/$USER_ID/permissions" "" 200 "Get User Permissions"

# Cache User Permissions
test_endpoint "POST" "$BASE_URL/$USER_ID/permissions/cache" "" 200 "Cache User Permissions"

# Check User Permission
test_endpoint "GET" "$BASE_URL/$USER_ID/permissions/check?permission=user:read" "" 200 "Check User Permission"

# Evict User Permissions Cache
test_endpoint "DELETE" "$BASE_URL/$USER_ID/permissions/cache" "" 200 "Evict User Permissions Cache"

print_separator

# Test 5: Role Management
echo "=== ROLE MANAGEMENT ==="

# Get User Roles
test_endpoint "GET" "$BASE_URL/$USER_ID/roles" "" 200 "Get User Roles"

# Check User Role
test_endpoint "GET" "$BASE_URL/$USER_ID/roles/check?role=USER" "" 200 "Check User Role"

print_separator

# Test 6: Validation
echo "=== VALIDATION ==="

# Check Username Availability (should be false - username exists)
test_endpoint "GET" "$BASE_URL/username/$TEST_USERNAME/available" "" 200 "Check Username Availability (Existing)"

# Check Username Availability (should be true - username doesn't exist)
test_endpoint "GET" "$BASE_URL/username/nonexistent_user/available" "" 200 "Check Username Availability (Non-existing)"

# Check Email Availability (should be false - email exists)
test_endpoint "GET" "$BASE_URL/email/$TEST_EMAIL/available" "" 200 "Check Email Availability (Existing)"

# Check Email Availability (should be true - email doesn't exist)
test_endpoint "GET" "$BASE_URL/email/nonexistent@example.com/available" "" 200 "Check Email Availability (Non-existing)"

print_separator

# Test 7: Error Scenarios
echo "=== ERROR SCENARIOS ==="

# Test 404 - Get non-existent user
test_endpoint "GET" "$BASE_URL/550e8400-e29b-41d4-a716-446655440000" "" 404 "Get Non-existent User"

# Test 400 - Create user with invalid data
invalid_user_data='{
    "username": "a",
    "email": "invalid-email",
    "password": "123"
}'
test_endpoint "POST" "$BASE_URL" "$invalid_user_data" 400 "Create User with Invalid Data"

# Test 409 - Create user with existing username
test_endpoint "POST" "$BASE_URL" "$create_user_data" 409 "Create User with Existing Username"

print_separator

# Test 8: Cleanup
echo "=== CLEANUP ==="

# Delete User
test_endpoint "DELETE" "$BASE_URL/$USER_ID" "" 204 "Delete User"

# Verify user is deleted
test_endpoint "GET" "$BASE_URL/$USER_ID" "" 404 "Verify User Deleted"

print_separator

# Test Summary
print_info "API Testing Complete!"
print_info "All major endpoints have been tested with various scenarios."
print_info "Review the output above for any failures."

# Additional test scenarios script
cat > additional_test_scenarios.sh << 'EOF'
#!/bin/bash

# Additional Test Scenarios
# This script contains more complex test scenarios

# Test concurrent user creation
echo "=== CONCURRENT USER CREATION TEST ==="
for i in {1..5}; do
    {
        username="concurrent_user_$i"
        email="concurrent_$i@example.com"
        
        curl -s -X POST http://localhost:8080/api/v1/users \
            -H "Content-Type: application/json" \
            -H "X-Tenant-ID: tenant1" \
            -H "X-Domain-ID: domain1" \
            -d '{
                "username": "'$username'",
                "email": "'$email'",
                "password": "Password123!",
                "firstName": "Concurrent",
                "lastName": "User'$i'"
            }' | jq .
    } &
done
wait

# Test batch operations
echo "=== BATCH OPERATIONS TEST ==="
# Create multiple users and test batch retrieval
users_created=()
for i in {1..3}; do
    username="batch_user_$i"
    email="batch_$i@example.com"
    
    response=$(curl -s -X POST http://localhost:8080/api/v1/users \
        -H "Content-Type: application/json" \
        -H "X-Tenant-ID: tenant1" \
        -H "X-Domain-ID: domain1" \
        -d '{
            "username": "'$username'",
            "email": "'$email'",
            "password": "Password123!",
            "firstName": "Batch",
            "lastName": "User'$i'"
        }')
    
    user_id=$(echo "$response" | jq -r '.id')
    users_created+=("$user_id")
    echo "Created user: $user_id"
done

# Test search functionality
echo "Testing search functionality..."
curl -s -X GET "http://localhost:8080/api/v1/users/search?q=batch" \
    -H "X-Tenant-ID: tenant1" \
    -H "X-Domain-ID: domain1" | jq .

# Cleanup batch users
for user_id in "${users_created[@]}"; do
    curl -s -X DELETE "http://localhost:8080/api/v1/users/$user_id" \
        -H "X-Tenant-ID: tenant1" \
        -H "X-Domain-ID: domain1"
    echo "Deleted user: $user_id"
done

EOF

chmod +x additional_test_scenarios.sh

print_info "Additional test scenarios script created: additional_test_scenarios.sh"
print_info "Run it with: ./additional_test_scenarios.sh"
