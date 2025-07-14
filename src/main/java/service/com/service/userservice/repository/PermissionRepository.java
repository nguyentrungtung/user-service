package service.com.service.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.com.service.userservice.entity.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    
    // Find permission by name within tenant
    Optional<Permission> findByNameAndTenantId(String name, String tenantId);
    
    // Find permission by name within tenant and domain
    Optional<Permission> findByNameAndTenantIdAndResourceDomain(String name, String tenantId, String resourceDomain);
    
    // Find permission by resource and action within tenant
    Optional<Permission> findByResourceAndActionAndTenantId(String resource, String action, String tenantId);
    
    // Find permission by resource and action within tenant and domain
    Optional<Permission> findByResourceAndActionAndTenantIdAndResourceDomain(String resource, String action, 
                                                                           String tenantId, String resourceDomain);
    
    // Find permission by ID within tenant
    Optional<Permission> findByIdAndTenantId(UUID id, String tenantId);
    
    // Find permission by ID within tenant and domain
    Optional<Permission> findByIdAndTenantIdAndResourceDomain(UUID id, String tenantId, String resourceDomain);
    
    // Find all permissions within tenant
    List<Permission> findByTenantId(String tenantId);
    
    // Find all permissions within tenant and domain
    List<Permission> findByTenantIdAndResourceDomain(String tenantId, String resourceDomain);
    
    // Find permissions by resource within tenant
    List<Permission> findByResourceAndTenantId(String resource, String tenantId);
    
    // Find permissions by resource within tenant and domain
    List<Permission> findByResourceAndTenantIdAndResourceDomain(String resource, String tenantId, String resourceDomain);
    
    // Find active permissions within tenant
    List<Permission> findByTenantIdAndIsActive(String tenantId, Boolean isActive);
    
    // Find active permissions within tenant and domain
    List<Permission> findByTenantIdAndResourceDomainAndIsActive(String tenantId, String resourceDomain, Boolean isActive);
    
    // Check if permission name exists in tenant
    boolean existsByNameAndTenantId(String name, String tenantId);
    
    // Check if permission name exists in tenant and domain
    boolean existsByNameAndTenantIdAndResourceDomain(String name, String tenantId, String resourceDomain);
    
    // Check if permission resource:action exists in tenant
    boolean existsByResourceAndActionAndTenantId(String resource, String action, String tenantId);
    
    // Check if permission resource:action exists in tenant and domain
    boolean existsByResourceAndActionAndTenantIdAndResourceDomain(String resource, String action, 
                                                                String tenantId, String resourceDomain);
    
    // Get user permissions through roles
    @Query("SELECT DISTINCT p FROM Permission p " +
           "JOIN p.roles r " +
           "JOIN r.users u " +
           "WHERE u.id = :userId AND u.tenantId = :tenantId AND p.isActive = true")
    List<Permission> findPermissionsByUserId(@Param("userId") UUID userId, @Param("tenantId") String tenantId);
    
    // Get user permissions through roles within domain
    @Query("SELECT DISTINCT p FROM Permission p " +
           "JOIN p.roles r " +
           "JOIN r.users u " +
           "WHERE u.id = :userId AND u.tenantId = :tenantId AND u.resourceDomain = :resourceDomain AND p.isActive = true")
    List<Permission> findPermissionsByUserIdAndDomain(@Param("userId") UUID userId, 
                                                     @Param("tenantId") String tenantId, 
                                                     @Param("resourceDomain") String resourceDomain);
    
    // Get all user permissions (through roles and direct)
    @Query("SELECT DISTINCT p FROM Permission p " +
           "WHERE p.id IN (" +
           "  SELECT rp.id FROM Permission rp " +
           "  JOIN rp.roles r " +
           "  JOIN r.users u " +
           "  WHERE u.id = :userId AND u.tenantId = :tenantId" +
           "  UNION " +
           "  SELECT up.id FROM Permission up " +
           "  JOIN up.users u " +
           "  WHERE u.id = :userId AND u.tenantId = :tenantId" +
           ") AND p.isActive = true")
    List<Permission> findAllPermissionsByUserId(@Param("userId") UUID userId, @Param("tenantId") String tenantId);
    
    // Get all user permissions within domain
    @Query("SELECT DISTINCT p FROM Permission p " +
           "WHERE p.id IN (" +
           "  SELECT rp.id FROM Permission rp " +
           "  JOIN rp.roles r " +
           "  JOIN r.users u " +
           "  WHERE u.id = :userId AND u.tenantId = :tenantId AND u.resourceDomain = :resourceDomain" +
           "  UNION " +
           "  SELECT up.id FROM Permission up " +
           "  JOIN up.users u " +
           "  WHERE u.id = :userId AND u.tenantId = :tenantId AND u.resourceDomain = :resourceDomain" +
           ") AND p.isActive = true")
    List<Permission> findAllPermissionsByUserIdAndDomain(@Param("userId") UUID userId, 
                                                        @Param("tenantId") String tenantId, 
                                                        @Param("resourceDomain") String resourceDomain);
    
    // Search permissions within tenant
    @Query("SELECT p FROM Permission p WHERE p.tenantId = :tenantId AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.resource) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.action) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Permission> searchPermissions(@Param("tenantId") String tenantId, @Param("searchTerm") String searchTerm);
    
    // Search permissions within tenant and domain
    @Query("SELECT p FROM Permission p WHERE p.tenantId = :tenantId AND p.resourceDomain = :resourceDomain AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.resource) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.action) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Permission> searchPermissionsByDomain(@Param("tenantId") String tenantId, 
                                              @Param("resourceDomain") String resourceDomain, 
                                              @Param("searchTerm") String searchTerm);
}
