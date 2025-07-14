package service.com.service.userservice.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import service.com.service.userservice.dto.CreateUserRequest;
import service.com.service.userservice.dto.UserDto;
import service.com.service.userservice.dto.UserPermissionsDto;
import service.com.service.userservice.entity.User;
import service.com.service.userservice.exception.UserAlreadyExistsException;
import service.com.service.userservice.exception.UserNotFoundException;
import service.com.service.userservice.repository.UserRepository;
import service.com.service.userservice.repository.RoleRepository;
import service.com.service.userservice.repository.PermissionRepository;
import service.com.service.userservice.service.CacheService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private PermissionRepository permissionRepository;
    
    @Mock
    private CacheService cacheService;
    
    @Mock
    private ModelMapper modelMapper;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private String tenantId;
    private String resourceDomain;
    private UUID userId;
    private CreateUserRequest createUserRequest;
    private User user;
    private UserDto userDto;
    
    @BeforeEach
    void setUp() {
        tenantId = "tenant1";
        resourceDomain = "domain1";
        userId = UUID.randomUUID();
        
        createUserRequest = CreateUserRequest.builder()
            .username("testuser")
            .email("test@example.com")
            .password("password123")
            .firstName("Test")
            .lastName("User")
            .isActive(true)
            .build();
        
        user = User.builder()
            .id(userId)
            .tenantId(tenantId)
            .resourceDomain(resourceDomain)
            .username("testuser")
            .email("test@example.com")
            .passwordHash("hashedpassword")
            .firstName("Test")
            .lastName("User")
            .isActive(true)
            .isEmailVerified(false)
            .build();
        
        userDto = UserDto.builder()
            .id(userId)
            .tenantId(tenantId)
            .resourceDomain(resourceDomain)
            .username("testuser")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .isActive(true)
            .isEmailVerified(false)
            .build();
    }
    
    @Test
    void createUser_Success() {
        // Given
        when(userRepository.existsByUsernameAndTenantIdAndResourceDomain(
            createUserRequest.getUsername(), tenantId, resourceDomain)).thenReturn(false);
        when(userRepository.existsByEmailAndTenantIdAndResourceDomain(
            createUserRequest.getEmail(), tenantId, resourceDomain)).thenReturn(false);
        when(passwordEncoder.encode(createUserRequest.getPassword())).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        
        // When
        UserDto result = userService.createUser(createUserRequest, tenantId, resourceDomain);
        
        // Then
        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(createUserRequest.getPassword());
    }
    
    @Test
    void createUser_UsernameAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsernameAndTenantIdAndResourceDomain(
            createUserRequest.getUsername(), tenantId, resourceDomain)).thenReturn(true);
        
        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> 
            userService.createUser(createUserRequest, tenantId, resourceDomain));
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsernameAndTenantIdAndResourceDomain(
            createUserRequest.getUsername(), tenantId, resourceDomain)).thenReturn(false);
        when(userRepository.existsByEmailAndTenantIdAndResourceDomain(
            createUserRequest.getEmail(), tenantId, resourceDomain)).thenReturn(true);
        
        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> 
            userService.createUser(createUserRequest, tenantId, resourceDomain));
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void getUserById_Success() {
        // Given
        when(userRepository.findByIdAndTenantIdAndResourceDomain(userId, tenantId, resourceDomain))
            .thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        
        // When
        UserDto result = userService.getUserById(userId, tenantId, resourceDomain);
        
        // Then
        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getUsername(), result.getUsername());
    }
    
    @Test
    void getUserById_NotFound_ThrowsException() {
        // Given
        when(userRepository.findByIdAndTenantIdAndResourceDomain(userId, tenantId, resourceDomain))
            .thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(UserNotFoundException.class, () -> 
            userService.getUserById(userId, tenantId, resourceDomain));
    }
    
    @Test
    void getUserByUsername_Success() {
        // Given
        when(userRepository.findByUsernameAndTenantIdAndResourceDomain(
            user.getUsername(), tenantId, resourceDomain)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        
        // When
        UserDto result = userService.getUserByUsername(user.getUsername(), tenantId, resourceDomain);
        
        // Then
        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
    }
    
    @Test
    void deleteUser_Success() {
        // Given
        when(userRepository.findByIdAndTenantIdAndResourceDomain(userId, tenantId, resourceDomain))
            .thenReturn(Optional.of(user));
        
        // When
        userService.deleteUser(userId, tenantId, resourceDomain);
        
        // Then
        verify(userRepository).delete(user);
        verify(cacheService).evictUserPermissionsCache(userId, tenantId, resourceDomain);
    }
    
    @Test
    void activateUser_Success() {
        // Given
        when(userRepository.findByIdAndTenantIdAndResourceDomain(userId, tenantId, resourceDomain))
            .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // When
        userService.activateUser(userId, tenantId, resourceDomain);
        
        // Then
        verify(userRepository).save(any(User.class));
        assertTrue(user.getIsActive());
    }
    
    @Test
    void deactivateUser_Success() {
        // Given
        when(userRepository.findByIdAndTenantIdAndResourceDomain(userId, tenantId, resourceDomain))
            .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // When
        userService.deactivateUser(userId, tenantId, resourceDomain);
        
        // Then
        verify(userRepository).save(any(User.class));
        verify(cacheService).evictUserPermissionsCache(userId, tenantId, resourceDomain);
        assertFalse(user.getIsActive());
    }
    
    @Test
    void cacheUserPermissions_Success() {
        // Given
        UserPermissionsDto permissions = UserPermissionsDto.builder()
            .userId(userId)
            .username(user.getUsername())
            .tenantId(tenantId)
            .resourceDomain(resourceDomain)
            .build();
        
        when(userRepository.findUserWithRolesAndPermissionsByDomain(userId, tenantId, resourceDomain))
            .thenReturn(Optional.of(user));
        
        // When
        userService.cacheUserPermissions(userId, tenantId, resourceDomain);
        
        // Then
        verify(cacheService).cacheUserPermissions(eq(userId), eq(tenantId), eq(resourceDomain), any(UserPermissionsDto.class));
    }
    
    @Test
    void getCachedUserPermissions_CacheHit() {
        // Given
        UserPermissionsDto cachedPermissions = UserPermissionsDto.builder()
            .userId(userId)
            .username(user.getUsername())
            .tenantId(tenantId)
            .resourceDomain(resourceDomain)
            .build();
        
        when(cacheService.getCachedUserPermissions(userId, tenantId, resourceDomain))
            .thenReturn(cachedPermissions);
        
        // When
        UserPermissionsDto result = userService.getCachedUserPermissions(userId, tenantId, resourceDomain);
        
        // Then
        assertNotNull(result);
        assertEquals(cachedPermissions.getUserId(), result.getUserId());
        verify(cacheService).getCachedUserPermissions(userId, tenantId, resourceDomain);
    }
    
    @Test
    void getCachedUserPermissions_CacheMiss() {
        // Given
        when(cacheService.getCachedUserPermissions(userId, tenantId, resourceDomain))
            .thenReturn(null);
        when(userRepository.findUserWithRolesAndPermissionsByDomain(userId, tenantId, resourceDomain))
            .thenReturn(Optional.of(user));
        
        // When
        UserPermissionsDto result = userService.getCachedUserPermissions(userId, tenantId, resourceDomain);
        
        // Then
        assertNotNull(result);
        verify(cacheService).getCachedUserPermissions(userId, tenantId, resourceDomain);
        verify(cacheService).cacheUserPermissions(eq(userId), eq(tenantId), eq(resourceDomain), any(UserPermissionsDto.class));
    }
    
    @Test
    void isUsernameAvailable_Available() {
        // Given
        when(userRepository.existsByUsernameAndTenantIdAndResourceDomain(
            "newuser", tenantId, resourceDomain)).thenReturn(false);
        
        // When
        boolean result = userService.isUsernameAvailable("newuser", tenantId, resourceDomain);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void isUsernameAvailable_NotAvailable() {
        // Given
        when(userRepository.existsByUsernameAndTenantIdAndResourceDomain(
            "existinguser", tenantId, resourceDomain)).thenReturn(true);
        
        // When
        boolean result = userService.isUsernameAvailable("existinguser", tenantId, resourceDomain);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void isEmailAvailable_Available() {
        // Given
        when(userRepository.existsByEmailAndTenantIdAndResourceDomain(
            "new@example.com", tenantId, resourceDomain)).thenReturn(false);
        
        // When
        boolean result = userService.isEmailAvailable("new@example.com", tenantId, resourceDomain);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void isEmailAvailable_NotAvailable() {
        // Given
        when(userRepository.existsByEmailAndTenantIdAndResourceDomain(
            "existing@example.com", tenantId, resourceDomain)).thenReturn(true);
        
        // When
        boolean result = userService.isEmailAvailable("existing@example.com", tenantId, resourceDomain);
        
        // Then
        assertFalse(result);
    }
}
