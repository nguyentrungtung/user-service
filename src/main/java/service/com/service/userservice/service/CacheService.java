package service.com.service.userservice.service;

import service.com.service.userservice.dto.UserPermissionsDto;

import java.util.List;
import java.util.UUID;

public interface CacheService {
    
    // Permission caching
    void cacheUserPermissions(UUID userId, String tenantId, String resourceDomain, UserPermissionsDto permissions);
    UserPermissionsDto getCachedUserPermissions(UUID userId, String tenantId, String resourceDomain);
    void evictUserPermissionsCache(UUID userId, String tenantId, String resourceDomain);
    void evictAllUserPermissionsCache(String tenantId);
    
    // JWT blacklisting
    void blacklistJWT(String jti, long expiration);
    boolean isJWTBlacklisted(String jti);
    void cleanupExpiredJWTs();
    
    // Session management
    void cacheUserSession(UUID userId, String sessionId, String tenantId, String resourceDomain);
    void evictUserSession(UUID userId, String sessionId, String tenantId, String resourceDomain);
    List<String> getUserSessions(UUID userId, String tenantId, String resourceDomain);
    void evictAllUserSessions(UUID userId, String tenantId, String resourceDomain);
    
    // Cache statistics
    boolean isPermissionCacheAvailable(UUID userId, String tenantId, String resourceDomain);
    long getPermissionCacheTTL(UUID userId, String tenantId, String resourceDomain);
    
    // Cache management
    void clearAllCache();
    void clearTenantCache(String tenantId);
    void clearDomainCache(String tenantId, String resourceDomain);
}
