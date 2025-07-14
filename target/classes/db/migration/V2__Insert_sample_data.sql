-- V2__Insert_sample_data.sql
-- Insert sample data for testing

-- Insert sample tenant data
INSERT INTO users (id, tenant_id, resource_domain, username, email, password_hash, first_name, last_name, is_active, is_email_verified, created_by) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'tenant1', 'domain1', 'admin', 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin', 'User', true, true, 'system'),
('550e8400-e29b-41d4-a716-446655440002', 'tenant1', 'domain1', 'user1', 'user1@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'John', 'Doe', true, true, 'system'),
('550e8400-e29b-41d4-a716-446655440003', 'tenant2', 'domain2', 'admin', 'admin@tenant2.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin', 'User2', true, true, 'system');

-- Insert sample roles
INSERT INTO roles (id, tenant_id, resource_domain, name, description, is_active, created_by) VALUES
('550e8400-e29b-41d4-a716-446655440010', 'tenant1', 'domain1', 'ADMIN', 'Administrator role with full permissions', true, 'system'),
('550e8400-e29b-41d4-a716-446655440011', 'tenant1', 'domain1', 'USER', 'Standard user role', true, 'system'),
('550e8400-e29b-41d4-a716-446655440012', 'tenant1', 'domain1', 'VIEWER', 'Read-only access role', true, 'system'),
('550e8400-e29b-41d4-a716-446655440013', 'tenant2', 'domain2', 'ADMIN', 'Administrator role for tenant2', true, 'system');

-- Insert sample permissions
INSERT INTO permissions (id, tenant_id, resource_domain, name, resource, action, description, is_active, created_by) VALUES
-- User management permissions
('550e8400-e29b-41d4-a716-446655440020', 'tenant1', 'domain1', 'USER_READ', 'user', 'read', 'Read user information', true, 'system'),
('550e8400-e29b-41d4-a716-446655440021', 'tenant1', 'domain1', 'USER_WRITE', 'user', 'write', 'Create and update users', true, 'system'),
('550e8400-e29b-41d4-a716-446655440022', 'tenant1', 'domain1', 'USER_DELETE', 'user', 'delete', 'Delete users', true, 'system'),
('550e8400-e29b-41d4-a716-446655440023', 'tenant1', 'domain1', 'USER_ADMIN', 'user', 'admin', 'Full user administration', true, 'system'),

-- Role management permissions
('550e8400-e29b-41d4-a716-446655440024', 'tenant1', 'domain1', 'ROLE_READ', 'role', 'read', 'Read role information', true, 'system'),
('550e8400-e29b-41d4-a716-446655440025', 'tenant1', 'domain1', 'ROLE_WRITE', 'role', 'write', 'Create and update roles', true, 'system'),
('550e8400-e29b-41d4-a716-446655440026', 'tenant1', 'domain1', 'ROLE_DELETE', 'role', 'delete', 'Delete roles', true, 'system'),

-- Permission management permissions
('550e8400-e29b-41d4-a716-446655440027', 'tenant1', 'domain1', 'PERMISSION_READ', 'permission', 'read', 'Read permission information', true, 'system'),
('550e8400-e29b-41d4-a716-446655440028', 'tenant1', 'domain1', 'PERMISSION_WRITE', 'permission', 'write', 'Create and update permissions', true, 'system'),
('550e8400-e29b-41d4-a716-446655440029', 'tenant1', 'domain1', 'PERMISSION_DELETE', 'permission', 'delete', 'Delete permissions', true, 'system'),

-- Tenant2 permissions
('550e8400-e29b-41d4-a716-446655440030', 'tenant2', 'domain2', 'USER_READ', 'user', 'read', 'Read user information', true, 'system'),
('550e8400-e29b-41d4-a716-446655440031', 'tenant2', 'domain2', 'USER_WRITE', 'user', 'write', 'Create and update users', true, 'system'),
('550e8400-e29b-41d4-a716-446655440032', 'tenant2', 'domain2', 'USER_ADMIN', 'user', 'admin', 'Full user administration', true, 'system');

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id, assigned_by) VALUES
('550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440010', 'system'), -- admin -> ADMIN
('550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440011', 'system'), -- user1 -> USER
('550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440013', 'system'); -- admin@tenant2 -> ADMIN

-- Assign permissions to roles
INSERT INTO role_permissions (role_id, permission_id, assigned_by) VALUES
-- ADMIN role permissions (tenant1)
('550e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440020', 'system'), -- USER_READ
('550e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440021', 'system'), -- USER_WRITE
('550e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440022', 'system'), -- USER_DELETE
('550e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440023', 'system'), -- USER_ADMIN
('550e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440024', 'system'), -- ROLE_READ
('550e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440025', 'system'), -- ROLE_WRITE
('550e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440026', 'system'), -- ROLE_DELETE
('550e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440027', 'system'), -- PERMISSION_READ
('550e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440028', 'system'), -- PERMISSION_WRITE
('550e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440029', 'system'), -- PERMISSION_DELETE

-- USER role permissions (tenant1)
('550e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440020', 'system'), -- USER_READ
('550e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440024', 'system'), -- ROLE_READ
('550e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440027', 'system'), -- PERMISSION_READ

-- VIEWER role permissions (tenant1)
('550e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440020', 'system'), -- USER_READ
('550e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440024', 'system'), -- ROLE_READ
('550e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440027', 'system'), -- PERMISSION_READ

-- ADMIN role permissions (tenant2)
('550e8400-e29b-41d4-a716-446655440013', '550e8400-e29b-41d4-a716-446655440030', 'system'), -- USER_READ
('550e8400-e29b-41d4-a716-446655440013', '550e8400-e29b-41d4-a716-446655440031', 'system'), -- USER_WRITE
('550e8400-e29b-41d4-a716-446655440013', '550e8400-e29b-41d4-a716-446655440032', 'system'); -- USER_ADMIN
