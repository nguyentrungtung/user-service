package service.com.service.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.com.service.userservice.entity.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    
    // Find role by name within tenant
    Optional<Role> findByNameAndTenantId(String name, String tenantId);
    
    // Find role by name within tenant and domain
    Optional<Role> findByNameAndTenantIdAndResourceDomain(String name, String tenantId, String resourceDomain);
    
    // Find role by ID within tenant
    Optional<Role> findByIdAndTenantId(UUID id, String tenantId);
    
    // Find role by ID within tenant and domain
    Optional<Role> findByIdAndTenantIdAndResourceDomain(UUID id, String tenantId, String resourceDomain);
    
    // Find all roles within tenant
    List<Role> findByTenantId(String tenantId);
    
    // Find all roles within tenant and domain
    List<Role> findByTenantIdAndResourceDomain(String tenantId, String resourceDomain);
    
    // Find active roles within tenant
    List<Role> findByTenantIdAndIsActive(String tenantId, Boolean isActive);
    
    // Find active roles within tenant and domain
    List<Role> findByTenantIdAndResourceDomainAndIsActive(String tenantId, String resourceDomain, Boolean isActive);
    
    // Check if role name exists in tenant
    boolean existsByNameAndTenantId(String name, String tenantId);
    
    // Check if role name exists in tenant and domain
    boolean existsByNameAndTenantIdAndResourceDomain(String name, String tenantId, String resourceDomain);
    
    // Get role with permissions
    @Query("SELECT DISTINCT r FROM Role r " +
           "LEFT JOIN FETCH r.permissions " +
           "WHERE r.id = :roleId AND r.tenantId = :tenantId")
    Optional<Role> findRoleWithPermissions(@Param("roleId") UUID roleId, @Param("tenantId") String tenantId);
    
    // Get role with permissions by name
    @Query("SELECT DISTINCT r FROM Role r " +
           "LEFT JOIN FETCH r.permissions " +
           "WHERE r.name = :roleName AND r.tenantId = :tenantId")
    Optional<Role> findRoleWithPermissionsByName(@Param("roleName") String roleName, @Param("tenantId") String tenantId);
    
    // Get role with permissions within domain
    @Query("SELECT DISTINCT r FROM Role r " +
           "LEFT JOIN FETCH r.permissions " +
           "WHERE r.id = :roleId AND r.tenantId = :tenantId AND r.resourceDomain = :resourceDomain")
    Optional<Role> findRoleWithPermissionsByDomain(@Param("roleId") UUID roleId, 
                                                  @Param("tenantId") String tenantId, 
                                                  @Param("resourceDomain") String resourceDomain);
    
    // Search roles by name within tenant
    @Query("SELECT r FROM Role r WHERE r.tenantId = :tenantId AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Role> searchRoles(@Param("tenantId") String tenantId, @Param("searchTerm") String searchTerm);
    
    // Search roles by name within tenant and domain
    @Query("SELECT r FROM Role r WHERE r.tenantId = :tenantId AND r.resourceDomain = :resourceDomain AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Role> searchRolesByDomain(@Param("tenantId") String tenantId, 
                                  @Param("resourceDomain") String resourceDomain, 
                                  @Param("searchTerm") String searchTerm);
}
