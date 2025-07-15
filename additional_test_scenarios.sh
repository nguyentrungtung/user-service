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

