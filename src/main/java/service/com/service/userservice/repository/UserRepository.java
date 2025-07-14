package service.com.service.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.com.service.userservice.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    // Find user by username within tenant
    Optional<User> findByUsernameAndTenantId(String username, String tenantId);
    
    // Find user by email within tenant
    Optional<User> findByEmailAndTenantId(String email, String tenantId);
    
    // Find user by username, tenant and domain
    Optional<User> findByUsernameAndTenantIdAndResourceDomain(String username, String tenantId, String resourceDomain);
    
    // Find user by email, tenant and domain
    Optional<User> findByEmailAndTenantIdAndResourceDomain(String email, String tenantId, String resourceDomain);
    
    // Find user by ID within tenant
    Optional<User> findByIdAndTenantId(UUID id, String tenantId);
    
    // Find user by ID within tenant and domain
    Optional<User> findByIdAndTenantIdAndResourceDomain(UUID id, String tenantId, String resourceDomain);
    
    // Find all users within tenant
    List<User> findByTenantId(String tenantId);
    
    // Find all users within tenant and domain
    List<User> findByTenantIdAndResourceDomain(String tenantId, String resourceDomain);
    
    // Find active users within tenant
    List<User> findByTenantIdAndIsActive(String tenantId, Boolean isActive);
    
    // Find active users within tenant and domain
    List<User> findByTenantIdAndResourceDomainAndIsActive(String tenantId, String resourceDomain, Boolean isActive);
    
    // Check if username exists in tenant
    boolean existsByUsernameAndTenantId(String username, String tenantId);
    
    // Check if email exists in tenant
    boolean existsByEmailAndTenantId(String email, String tenantId);
    
    // Check if username exists in tenant and domain
    boolean existsByUsernameAndTenantIdAndResourceDomain(String username, String tenantId, String resourceDomain);
    
    // Check if email exists in tenant and domain
    boolean existsByEmailAndTenantIdAndResourceDomain(String email, String tenantId, String resourceDomain);
    
    // Get user with roles and permissions
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.roles r " +
           "LEFT JOIN FETCH r.permissions " +
           "LEFT JOIN FETCH u.permissions " +
           "WHERE u.id = :userId AND u.tenantId = :tenantId")
    Optional<User> findUserWithRolesAndPermissions(@Param("userId") UUID userId, @Param("tenantId") String tenantId);
    
    // Get user with roles and permissions by username
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.roles r " +
           "LEFT JOIN FETCH r.permissions " +
           "LEFT JOIN FETCH u.permissions " +
           "WHERE u.username = :username AND u.tenantId = :tenantId")
    Optional<User> findUserWithRolesAndPermissionsByUsername(@Param("username") String username, @Param("tenantId") String tenantId);
    
    // Get user with roles and permissions within domain
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.roles r " +
           "LEFT JOIN FETCH r.permissions " +
           "LEFT JOIN FETCH u.permissions " +
           "WHERE u.id = :userId AND u.tenantId = :tenantId AND u.resourceDomain = :resourceDomain")
    Optional<User> findUserWithRolesAndPermissionsByDomain(@Param("userId") UUID userId, 
                                                          @Param("tenantId") String tenantId, 
                                                          @Param("resourceDomain") String resourceDomain);
    
    // Search users by name within tenant
    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<User> searchUsers(@Param("tenantId") String tenantId, @Param("searchTerm") String searchTerm);
    
    // Search users by name within tenant and domain
    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND u.resourceDomain = :resourceDomain AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<User> searchUsersByDomain(@Param("tenantId") String tenantId, 
                                  @Param("resourceDomain") String resourceDomain, 
                                  @Param("searchTerm") String searchTerm);
}
