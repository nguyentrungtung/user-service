-- V2__Insert_sample_data.sql
-- Sample data for testing (H2 compatible)

-- Insert sample roles
INSERT INTO roles (id, tenant_id, resource_domain, name, description, is_active, created_by)
VALUES 
    (RANDOM_UUID(), 'tenant1', 'domain1', 'ADMIN', 'System Administrator', TRUE, 'system'),
    (RANDOM_UUID(), 'tenant1', 'domain1', 'USER', 'Regular User', TRUE, 'system'),
    (RANDOM_UUID(), 'tenant1', 'domain1', 'MANAGER', 'Manager Role', TRUE, 'system'),
    (RANDOM_UUID(), 'tenant2', 'domain1', 'ADMIN', 'System Administrator', TRUE, 'system'),
    (RANDOM_UUID(), 'tenant2', 'domain1', 'USER', 'Regular User', TRUE, 'system');

-- Insert sample permissions
INSERT INTO permissions (id, tenant_id, resource_domain, name, description, resource_type, action, is_active, created_by)
VALUES 
    (RANDOM_UUID(), 'tenant1', 'domain1', 'USER_CREATE', 'Create users', 'USER', 'CREATE', TRUE, 'system'),
    (RANDOM_UUID(), 'tenant1', 'domain1', 'USER_READ', 'Read users', 'USER', 'READ', TRUE, 'system'),
    (RANDOM_UUID(), 'tenant1', 'domain1', 'USER_UPDATE', 'Update users', 'USER', 'UPDATE', TRUE, 'system'),
    (RANDOM_UUID(), 'tenant1', 'domain1', 'USER_DELETE', 'Delete users', 'USER', 'DELETE', TRUE, 'system'),
    (RANDOM_UUID(), 'tenant1', 'domain1', 'ROLE_CREATE', 'Create roles', 'ROLE', 'CREATE', TRUE, 'system'),
    (RANDOM_UUID(), 'tenant1', 'domain1', 'ROLE_READ', 'Read roles', 'ROLE', 'READ', TRUE, 'system'),
    (RANDOM_UUID(), 'tenant1', 'domain1', 'ROLE_UPDATE', 'Update roles', 'ROLE', 'UPDATE', TRUE, 'system'),
    (RANDOM_UUID(), 'tenant1', 'domain1', 'ROLE_DELETE', 'Delete roles', 'ROLE', 'DELETE', TRUE, 'system'),
    (RANDOM_UUID(), 'tenant2', 'domain1', 'USER_CREATE', 'Create users', 'USER', 'CREATE', TRUE, 'system'),
    (RANDOM_UUID(), 'tenant2', 'domain1', 'USER_READ', 'Read users', 'USER', 'READ', TRUE, 'system');

-- Insert sample users
INSERT INTO users (id, tenant_id, resource_domain, username, email, password_hash, first_name, last_name, is_active, is_email_verified, created_by)
VALUES 
    (RANDOM_UUID(), 'tenant1', 'domain1', 'admin', 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin', 'User', TRUE, TRUE, 'system'),
    (RANDOM_UUID(), 'tenant1', 'domain1', 'user1', 'user1@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'User', 'One', TRUE, TRUE, 'system'),
    (RANDOM_UUID(), 'tenant1', 'domain1', 'manager1', 'manager1@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Manager', 'One', TRUE, TRUE, 'system'),
    (RANDOM_UUID(), 'tenant2', 'domain1', 'admin', 'admin@tenant2.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin', 'Two', TRUE, TRUE, 'system'),
    (RANDOM_UUID(), 'tenant2', 'domain1', 'user2', 'user2@tenant2.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'User', 'Two', TRUE, TRUE, 'system');

-- Assign roles to users for tenant1
INSERT INTO user_roles (id, user_id, role_id, tenant_id, resource_domain, assigned_by)
SELECT RANDOM_UUID(), u.id, r.id, u.tenant_id, u.resource_domain, 'system'
FROM users u, roles r
WHERE u.username = 'admin' AND u.tenant_id = 'tenant1' AND r.name = 'ADMIN' AND r.tenant_id = 'tenant1';

INSERT INTO user_roles (id, user_id, role_id, tenant_id, resource_domain, assigned_by)
SELECT RANDOM_UUID(), u.id, r.id, u.tenant_id, u.resource_domain, 'system'
FROM users u, roles r
WHERE u.username = 'user1' AND u.tenant_id = 'tenant1' AND r.name = 'USER' AND r.tenant_id = 'tenant1';

INSERT INTO user_roles (id, user_id, role_id, tenant_id, resource_domain, assigned_by)
SELECT RANDOM_UUID(), u.id, r.id, u.tenant_id, u.resource_domain, 'system'
FROM users u, roles r
WHERE u.username = 'manager1' AND u.tenant_id = 'tenant1' AND r.name = 'MANAGER' AND r.tenant_id = 'tenant1';

-- Assign roles to users for tenant2
INSERT INTO user_roles (id, user_id, role_id, tenant_id, resource_domain, assigned_by)
SELECT RANDOM_UUID(), u.id, r.id, u.tenant_id, u.resource_domain, 'system'
FROM users u, roles r
WHERE u.username = 'admin' AND u.tenant_id = 'tenant2' AND r.name = 'ADMIN' AND r.tenant_id = 'tenant2';

INSERT INTO user_roles (id, user_id, role_id, tenant_id, resource_domain, assigned_by)
SELECT RANDOM_UUID(), u.id, r.id, u.tenant_id, u.resource_domain, 'system'
FROM users u, roles r
WHERE u.username = 'user2' AND u.tenant_id = 'tenant2' AND r.name = 'USER' AND r.tenant_id = 'tenant2';

-- Assign permissions to ADMIN role for tenant1
INSERT INTO role_permissions (id, role_id, permission_id, tenant_id, resource_domain, assigned_by)
SELECT RANDOM_UUID(), r.id, p.id, r.tenant_id, r.resource_domain, 'system'
FROM roles r, permissions p
WHERE r.name = 'ADMIN' AND r.tenant_id = 'tenant1' AND p.tenant_id = 'tenant1';

-- Assign read permissions to USER role for tenant1
INSERT INTO role_permissions (id, role_id, permission_id, tenant_id, resource_domain, assigned_by)
SELECT RANDOM_UUID(), r.id, p.id, r.tenant_id, r.resource_domain, 'system'
FROM roles r, permissions p
WHERE r.name = 'USER' AND r.tenant_id = 'tenant1' AND p.name = 'USER_READ' AND p.tenant_id = 'tenant1';

-- Assign user management permissions to MANAGER role for tenant1
INSERT INTO role_permissions (id, role_id, permission_id, tenant_id, resource_domain, assigned_by)
SELECT RANDOM_UUID(), r.id, p.id, r.tenant_id, r.resource_domain, 'system'
FROM roles r, permissions p
WHERE r.name = 'MANAGER' AND r.tenant_id = 'tenant1' AND p.name IN ('USER_CREATE', 'USER_READ', 'USER_UPDATE') AND p.tenant_id = 'tenant1';

-- Assign basic permissions to roles for tenant2
INSERT INTO role_permissions (id, role_id, permission_id, tenant_id, resource_domain, assigned_by)
SELECT RANDOM_UUID(), r.id, p.id, r.tenant_id, r.resource_domain, 'system'
FROM roles r, permissions p
WHERE r.name = 'ADMIN' AND r.tenant_id = 'tenant2' AND p.tenant_id = 'tenant2';

INSERT INTO role_permissions (id, role_id, permission_id, tenant_id, resource_domain, assigned_by)
SELECT RANDOM_UUID(), r.id, p.id, r.tenant_id, r.resource_domain, 'system'
FROM roles r, permissions p
WHERE r.name = 'USER' AND r.tenant_id = 'tenant2' AND p.name = 'USER_READ' AND p.tenant_id = 'tenant2';
