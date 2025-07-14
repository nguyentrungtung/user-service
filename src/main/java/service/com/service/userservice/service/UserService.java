package service.com.service.userservice.service;

import service.com.service.userservice.dto.*;
import service.com.service.userservice.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    
    // User CRUD operations
    UserDto createUser(CreateUserRequest request, String tenantId, String resourceDomain);
    UserDto updateUser(UUID userId, UpdateUserRequest request, String tenantId, String resourceDomain);
    void deleteUser(UUID userId, String tenantId, String resourceDomain);
    UserDto getUserById(UUID userId, String tenantId, String resourceDomain);
    UserDto getUserByUsername(String username, String tenantId, String resourceDomain);
    List<UserDto> getAllUsers(String tenantId, String resourceDomain);
    List<UserDto> searchUsers(String searchTerm, String tenantId, String resourceDomain);
    
    // User authentication
    UserDto authenticateUser(String username, String password, String tenantId, String resourceDomain);
    void updateLastLogin(UUID userId, String tenantId);
    
    // User status management
    void activateUser(UUID userId, String tenantId, String resourceDomain);
    void deactivateUser(UUID userId, String tenantId, String resourceDomain);
    void verifyEmail(UUID userId, String tenantId, String resourceDomain);
    
    // Role management
    void assignRole(UUID userId, UUID roleId, String tenantId, String resourceDomain);
    void removeRole(UUID userId, UUID roleId, String tenantId, String resourceDomain);
    List<RoleDto> getUserRoles(UUID userId, String tenantId, String resourceDomain);
    
    // Permission management
    void assignPermission(UUID userId, UUID permissionId, String tenantId, String resourceDomain);
    void removePermission(UUID userId, UUID permissionId, String tenantId, String resourceDomain);
    List<PermissionDto> getUserPermissions(UUID userId, String tenantId, String resourceDomain);
    UserPermissionsDto getUserPermissionsWithRoles(UUID userId, String tenantId, String resourceDomain);
    
    // Permission caching
    void cacheUserPermissions(UUID userId, String tenantId, String resourceDomain);
    UserPermissionsDto getCachedUserPermissions(UUID userId, String tenantId, String resourceDomain);
    void evictUserPermissionsCache(UUID userId, String tenantId, String resourceDomain);
    
    // Validation methods
    boolean isUsernameAvailable(String username, String tenantId, String resourceDomain);
    boolean isEmailAvailable(String email, String tenantId, String resourceDomain);
    boolean hasPermission(UUID userId, String permission, String tenantId, String resourceDomain);
    boolean hasRole(UUID userId, String roleName, String tenantId, String resourceDomain);
    
    // Utility methods
    User getUserEntity(UUID userId, String tenantId, String resourceDomain);
    List<String> getAllPermissionStrings(UUID userId, String tenantId, String resourceDomain);
    List<String> getAllRoleNames(UUID userId, String tenantId, String resourceDomain);
}
