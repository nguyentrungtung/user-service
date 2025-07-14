package service.com.service.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.com.service.userservice.dto.*;
import service.com.service.userservice.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.info("Creating user with username: {} for tenant: {} and domain: {}", 
                request.getUsername(), tenantId, resourceDomain);
        
        UserDto createdUser = userService.createUser(request, tenantId, resourceDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable UUID userId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.debug("Getting user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        UserDto user = userService.getUserById(userId, tenantId, resourceDomain);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(
            @PathVariable String username,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.debug("Getting user with username: {} for tenant: {} and domain: {}", username, tenantId, resourceDomain);
        
        UserDto user = userService.getUserByUsername(username, tenantId, resourceDomain);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.info("Updating user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        UserDto updatedUser = userService.updateUser(userId, request, tenantId, resourceDomain);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID userId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.info("Deleting user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        userService.deleteUser(userId, tenantId, resourceDomain);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.debug("Getting all users for tenant: {} and domain: {}", tenantId, resourceDomain);
        
        List<UserDto> users = userService.getAllUsers(tenantId, resourceDomain);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(
            @RequestParam String q,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.debug("Searching users with term: {} for tenant: {} and domain: {}", q, tenantId, resourceDomain);
        
        List<UserDto> users = userService.searchUsers(q, tenantId, resourceDomain);
        return ResponseEntity.ok(users);
    }
    
    @PostMapping("/{userId}/activate")
    public ResponseEntity<Void> activateUser(
            @PathVariable UUID userId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.info("Activating user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        userService.activateUser(userId, tenantId, resourceDomain);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable UUID userId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.info("Deactivating user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        userService.deactivateUser(userId, tenantId, resourceDomain);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{userId}/verify-email")
    public ResponseEntity<Void> verifyEmail(
            @PathVariable UUID userId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.info("Verifying email for user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        userService.verifyEmail(userId, tenantId, resourceDomain);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{userId}/permissions")
    public ResponseEntity<UserPermissionsDto> getUserPermissions(
            @PathVariable UUID userId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.debug("Getting permissions for user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        UserPermissionsDto permissions = userService.getCachedUserPermissions(userId, tenantId, resourceDomain);
        return ResponseEntity.ok(permissions);
    }
    
    @PostMapping("/{userId}/permissions/cache")
    public ResponseEntity<Void> cacheUserPermissions(
            @PathVariable UUID userId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.info("Caching permissions for user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        userService.cacheUserPermissions(userId, tenantId, resourceDomain);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{userId}/permissions/cache")
    public ResponseEntity<Void> evictUserPermissionsCache(
            @PathVariable UUID userId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.info("Evicting permissions cache for user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        userService.evictUserPermissionsCache(userId, tenantId, resourceDomain);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<RoleDto>> getUserRoles(
            @PathVariable UUID userId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.debug("Getting roles for user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        List<RoleDto> roles = userService.getUserRoles(userId, tenantId, resourceDomain);
        return ResponseEntity.ok(roles);
    }
    
    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> assignRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.info("Assigning role {} to user {} for tenant: {} and domain: {}", roleId, userId, tenantId, resourceDomain);
        
        userService.assignRole(userId, roleId, tenantId, resourceDomain);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> removeRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.info("Removing role {} from user {} for tenant: {} and domain: {}", roleId, userId, tenantId, resourceDomain);
        
        userService.removeRole(userId, roleId, tenantId, resourceDomain);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{userId}/permissions/{permissionId}")
    public ResponseEntity<Void> assignPermission(
            @PathVariable UUID userId,
            @PathVariable UUID permissionId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.info("Assigning permission {} to user {} for tenant: {} and domain: {}", permissionId, userId, tenantId, resourceDomain);
        
        userService.assignPermission(userId, permissionId, tenantId, resourceDomain);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{userId}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermission(
            @PathVariable UUID userId,
            @PathVariable UUID permissionId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.info("Removing permission {} from user {} for tenant: {} and domain: {}", permissionId, userId, tenantId, resourceDomain);
        
        userService.removePermission(userId, permissionId, tenantId, resourceDomain);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{userId}/permissions/check")
    public ResponseEntity<Boolean> checkPermission(
            @PathVariable UUID userId,
            @RequestParam String permission,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.debug("Checking permission {} for user {} in tenant: {} and domain: {}", permission, userId, tenantId, resourceDomain);
        
        boolean hasPermission = userService.hasPermission(userId, permission, tenantId, resourceDomain);
        return ResponseEntity.ok(hasPermission);
    }
    
    @GetMapping("/{userId}/roles/check")
    public ResponseEntity<Boolean> checkRole(
            @PathVariable UUID userId,
            @RequestParam String role,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.debug("Checking role {} for user {} in tenant: {} and domain: {}", role, userId, tenantId, resourceDomain);
        
        boolean hasRole = userService.hasRole(userId, role, tenantId, resourceDomain);
        return ResponseEntity.ok(hasRole);
    }
    
    @GetMapping("/username/{username}/available")
    public ResponseEntity<Boolean> checkUsernameAvailability(
            @PathVariable String username,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.debug("Checking username availability: {} for tenant: {} and domain: {}", username, tenantId, resourceDomain);
        
        boolean isAvailable = userService.isUsernameAvailable(username, tenantId, resourceDomain);
        return ResponseEntity.ok(isAvailable);
    }
    
    @GetMapping("/email/{email}/available")
    public ResponseEntity<Boolean> checkEmailAvailability(
            @PathVariable String email,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Domain-ID") String resourceDomain) {
        
        log.debug("Checking email availability: {} for tenant: {} and domain: {}", email, tenantId, resourceDomain);
        
        boolean isAvailable = userService.isEmailAvailable(email, tenantId, resourceDomain);
        return ResponseEntity.ok(isAvailable);
    }
}
