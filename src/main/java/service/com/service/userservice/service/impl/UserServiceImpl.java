package service.com.service.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.com.service.userservice.dto.*;
import service.com.service.userservice.entity.User;
import service.com.service.userservice.entity.Role;
import service.com.service.userservice.entity.Permission;
import service.com.service.userservice.exception.UserNotFoundException;
import service.com.service.userservice.exception.UserAlreadyExistsException;
import service.com.service.userservice.exception.InvalidCredentialsException;
import service.com.service.userservice.repository.UserRepository;
import service.com.service.userservice.repository.RoleRepository;
import service.com.service.userservice.repository.PermissionRepository;
import service.com.service.userservice.service.UserService;
import service.com.service.userservice.service.CacheService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final CacheService cacheService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDto createUser(CreateUserRequest request, String tenantId, String resourceDomain) {
        log.info("Creating user with username: {} for tenant: {} and domain: {}", 
                request.getUsername(), tenantId, resourceDomain);
        
        // Check if username already exists
        if (userRepository.existsByUsernameAndTenantIdAndResourceDomain(
                request.getUsername(), tenantId, resourceDomain)) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmailAndTenantIdAndResourceDomain(
                request.getEmail(), tenantId, resourceDomain)) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }
        
        // Create user entity
        User user = User.builder()
            .tenantId(tenantId)
            .resourceDomain(resourceDomain)
            .username(request.getUsername())
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .isActive(request.getIsActive())
            .isEmailVerified(false)
            .createdBy("system")
            .build();
        
        User savedUser = userRepository.save(user);
        
        log.info("User created successfully with ID: {}", savedUser.getId());
        return modelMapper.map(savedUser, UserDto.class);
    }
    
    @Override
    public UserDto updateUser(UUID userId, UpdateUserRequest request, String tenantId, String resourceDomain) {
        log.info("Updating user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        User user = getUserEntity(userId, tenantId, resourceDomain);
        
        // Update fields if provided
        if (request.getEmail() != null) {
            // Check if email already exists for another user
            if (userRepository.existsByEmailAndTenantIdAndResourceDomain(
                    request.getEmail(), tenantId, resourceDomain)) {
                User existingUser = userRepository.findByEmailAndTenantIdAndResourceDomain(
                        request.getEmail(), tenantId, resourceDomain).orElse(null);
                if (existingUser != null && !existingUser.getId().equals(userId)) {
                    throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
                }
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }
        
        if (request.getIsEmailVerified() != null) {
            user.setIsEmailVerified(request.getIsEmailVerified());
        }
        
        user.setUpdatedBy("system");
        
        User updatedUser = userRepository.save(user);
        
        // Evict permissions cache
        cacheService.evictUserPermissionsCache(userId, tenantId, resourceDomain);
        
        log.info("User updated successfully with ID: {}", userId);
        return modelMapper.map(updatedUser, UserDto.class);
    }
    
    @Override
    public void deleteUser(UUID userId, String tenantId, String resourceDomain) {
        log.info("Deleting user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        User user = getUserEntity(userId, tenantId, resourceDomain);
        
        // Evict permissions cache
        cacheService.evictUserPermissionsCache(userId, tenantId, resourceDomain);
        
        userRepository.delete(user);
        
        log.info("User deleted successfully with ID: {}", userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(UUID userId, String tenantId, String resourceDomain) {
        log.debug("Getting user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        User user = getUserEntity(userId, tenantId, resourceDomain);
        return modelMapper.map(user, UserDto.class);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username, String tenantId, String resourceDomain) {
        log.debug("Getting user with username: {} for tenant: {} and domain: {}", username, tenantId, resourceDomain);
        
        User user = userRepository.findByUsernameAndTenantIdAndResourceDomain(username, tenantId, resourceDomain)
            .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        
        return modelMapper.map(user, UserDto.class);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(String tenantId, String resourceDomain) {
        log.debug("Getting all users for tenant: {} and domain: {}", tenantId, resourceDomain);
        
        List<User> users = userRepository.findByTenantIdAndResourceDomain(tenantId, resourceDomain);
        return users.stream()
            .map(user -> modelMapper.map(user, UserDto.class))
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> searchUsers(String searchTerm, String tenantId, String resourceDomain) {
        log.debug("Searching users with term: {} for tenant: {} and domain: {}", searchTerm, tenantId, resourceDomain);
        
        List<User> users = userRepository.searchUsersByDomain(tenantId, resourceDomain, searchTerm);
        return users.stream()
            .map(user -> modelMapper.map(user, UserDto.class))
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDto authenticateUser(String username, String password, String tenantId, String resourceDomain) {
        log.debug("Authenticating user with username: {} for tenant: {} and domain: {}", username, tenantId, resourceDomain);
        
        User user = userRepository.findByUsernameAndTenantIdAndResourceDomain(username, tenantId, resourceDomain)
            .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        
        if (!user.getIsActive()) {
            throw new InvalidCredentialsException("User account is inactive");
        }
        
        return modelMapper.map(user, UserDto.class);
    }
    
    @Override
    public void updateLastLogin(UUID userId, String tenantId) {
        log.debug("Updating last login for user ID: {} in tenant: {}", userId, tenantId);
        
        User user = userRepository.findByIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Override
    public void activateUser(UUID userId, String tenantId, String resourceDomain) {
        log.info("Activating user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        User user = getUserEntity(userId, tenantId, resourceDomain);
        user.setIsActive(true);
        user.setUpdatedBy("system");
        userRepository.save(user);
    }
    
    @Override
    public void deactivateUser(UUID userId, String tenantId, String resourceDomain) {
        log.info("Deactivating user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        User user = getUserEntity(userId, tenantId, resourceDomain);
        user.setIsActive(false);
        user.setUpdatedBy("system");
        userRepository.save(user);
        
        // Evict permissions cache
        cacheService.evictUserPermissionsCache(userId, tenantId, resourceDomain);
    }
    
    @Override
    public void verifyEmail(UUID userId, String tenantId, String resourceDomain) {
        log.info("Verifying email for user with ID: {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        User user = getUserEntity(userId, tenantId, resourceDomain);
        user.setIsEmailVerified(true);
        user.setUpdatedBy("system");
        userRepository.save(user);
    }
    
    @Override
    public void assignRole(UUID userId, UUID roleId, String tenantId, String resourceDomain) {
        log.info("Assigning role {} to user {} for tenant: {} and domain: {}", roleId, userId, tenantId, resourceDomain);
        
        User user = getUserEntity(userId, tenantId, resourceDomain);
        Role role = roleRepository.findByIdAndTenantIdAndResourceDomain(roleId, tenantId, resourceDomain)
            .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
        
        user.addRole(role);
        userRepository.save(user);
        
        // Evict permissions cache
        cacheService.evictUserPermissionsCache(userId, tenantId, resourceDomain);
    }
    
    @Override
    public void removeRole(UUID userId, UUID roleId, String tenantId, String resourceDomain) {
        log.info("Removing role {} from user {} for tenant: {} and domain: {}", roleId, userId, tenantId, resourceDomain);
        
        User user = getUserEntity(userId, tenantId, resourceDomain);
        Role role = roleRepository.findByIdAndTenantIdAndResourceDomain(roleId, tenantId, resourceDomain)
            .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
        
        user.removeRole(role);
        userRepository.save(user);
        
        // Evict permissions cache
        cacheService.evictUserPermissionsCache(userId, tenantId, resourceDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getUserRoles(UUID userId, String tenantId, String resourceDomain) {
        log.debug("Getting roles for user {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        User user = userRepository.findUserWithRolesAndPermissionsByDomain(userId, tenantId, resourceDomain)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        return user.getRoles().stream()
            .map(role -> modelMapper.map(role, RoleDto.class))
            .collect(Collectors.toList());
    }
    
    @Override
    public void assignPermission(UUID userId, UUID permissionId, String tenantId, String resourceDomain) {
        log.info("Assigning permission {} to user {} for tenant: {} and domain: {}", permissionId, userId, tenantId, resourceDomain);
        
        User user = getUserEntity(userId, tenantId, resourceDomain);
        Permission permission = permissionRepository.findByIdAndTenantIdAndResourceDomain(permissionId, tenantId, resourceDomain)
            .orElseThrow(() -> new RuntimeException("Permission not found with ID: " + permissionId));
        
        user.addPermission(permission);
        userRepository.save(user);
        
        // Evict permissions cache
        cacheService.evictUserPermissionsCache(userId, tenantId, resourceDomain);
    }
    
    @Override
    public void removePermission(UUID userId, UUID permissionId, String tenantId, String resourceDomain) {
        log.info("Removing permission {} from user {} for tenant: {} and domain: {}", permissionId, userId, tenantId, resourceDomain);
        
        User user = getUserEntity(userId, tenantId, resourceDomain);
        Permission permission = permissionRepository.findByIdAndTenantIdAndResourceDomain(permissionId, tenantId, resourceDomain)
            .orElseThrow(() -> new RuntimeException("Permission not found with ID: " + permissionId));
        
        user.removePermission(permission);
        userRepository.save(user);
        
        // Evict permissions cache
        cacheService.evictUserPermissionsCache(userId, tenantId, resourceDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PermissionDto> getUserPermissions(UUID userId, String tenantId, String resourceDomain) {
        log.debug("Getting permissions for user {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        List<Permission> permissions = permissionRepository.findAllPermissionsByUserIdAndDomain(userId, tenantId, resourceDomain);
        return permissions.stream()
            .map(permission -> modelMapper.map(permission, PermissionDto.class))
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserPermissionsDto getUserPermissionsWithRoles(UUID userId, String tenantId, String resourceDomain) {
        log.debug("Getting permissions with roles for user {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        User user = userRepository.findUserWithRolesAndPermissionsByDomain(userId, tenantId, resourceDomain)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        List<String> permissions = getAllPermissionStrings(userId, tenantId, resourceDomain);
        List<String> roles = getAllRoleNames(userId, tenantId, resourceDomain);
        
        return UserPermissionsDto.builder()
            .userId(userId)
            .username(user.getUsername())
            .tenantId(tenantId)
            .resourceDomain(resourceDomain)
            .permissions(permissions)
            .roles(roles)
            .build();
    }
    
    @Override
    public void cacheUserPermissions(UUID userId, String tenantId, String resourceDomain) {
        log.info("Caching permissions for user {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        UserPermissionsDto permissions = getUserPermissionsWithRoles(userId, tenantId, resourceDomain);
        cacheService.cacheUserPermissions(userId, tenantId, resourceDomain, permissions);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserPermissionsDto getCachedUserPermissions(UUID userId, String tenantId, String resourceDomain) {
        log.debug("Getting cached permissions for user {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        
        UserPermissionsDto cachedPermissions = cacheService.getCachedUserPermissions(userId, tenantId, resourceDomain);
        
        if (cachedPermissions == null) {
            log.debug("No cached permissions found, fetching from database");
            cachedPermissions = getUserPermissionsWithRoles(userId, tenantId, resourceDomain);
            cacheService.cacheUserPermissions(userId, tenantId, resourceDomain, cachedPermissions);
        }
        
        return cachedPermissions;
    }
    
    @Override
    public void evictUserPermissionsCache(UUID userId, String tenantId, String resourceDomain) {
        log.info("Evicting permissions cache for user {} for tenant: {} and domain: {}", userId, tenantId, resourceDomain);
        cacheService.evictUserPermissionsCache(userId, tenantId, resourceDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username, String tenantId, String resourceDomain) {
        return !userRepository.existsByUsernameAndTenantIdAndResourceDomain(username, tenantId, resourceDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email, String tenantId, String resourceDomain) {
        return !userRepository.existsByEmailAndTenantIdAndResourceDomain(email, tenantId, resourceDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(UUID userId, String permission, String tenantId, String resourceDomain) {
        UserPermissionsDto permissions = getCachedUserPermissions(userId, tenantId, resourceDomain);
        return permissions.hasPermission(permission);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(UUID userId, String roleName, String tenantId, String resourceDomain) {
        UserPermissionsDto permissions = getCachedUserPermissions(userId, tenantId, resourceDomain);
        return permissions.hasRole(roleName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserEntity(UUID userId, String tenantId, String resourceDomain) {
        return userRepository.findByIdAndTenantIdAndResourceDomain(userId, tenantId, resourceDomain)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllPermissionStrings(UUID userId, String tenantId, String resourceDomain) {
        List<Permission> permissions = permissionRepository.findAllPermissionsByUserIdAndDomain(userId, tenantId, resourceDomain);
        return permissions.stream()
            .map(Permission::getFullPermission)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllRoleNames(UUID userId, String tenantId, String resourceDomain) {
        User user = userRepository.findUserWithRolesAndPermissionsByDomain(userId, tenantId, resourceDomain)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        return user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toList());
    }
}
