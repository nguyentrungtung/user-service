package service.com.service.userservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import service.com.service.userservice.dto.UserPermissionsDto;
import service.com.service.userservice.service.CacheService;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheServiceImpl implements CacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${app.cache.permissions.ttl:3600}")
    private long permissionsCacheTtl;
    
    @Value("${app.cache.blacklist.ttl:86400}")
    private long blacklistCacheTtl;
    
    private static final String PERMISSIONS_KEY_PREFIX = "perm:";
    private static final String BLACKLIST_KEY_PREFIX = "blacklist:";
    private static final String SESSION_KEY_PREFIX = "session:";
    
    @Override
    public void cacheUserPermissions(UUID userId, String tenantId, String resourceDomain, UserPermissionsDto permissions) {
        String key = buildPermissionsKey(userId, tenantId, resourceDomain);
        
        try {
            String jsonValue = objectMapper.writeValueAsString(permissions);
            redisTemplate.opsForValue().set(key, jsonValue, permissionsCacheTtl, TimeUnit.SECONDS);
            log.debug("Cached permissions for user {} in tenant {} and domain {}", userId, tenantId, resourceDomain);
        } catch (JsonProcessingException e) {
            log.error("Error caching permissions for user {}: {}", userId, e.getMessage());
        }
    }
    
    @Override
    public UserPermissionsDto getCachedUserPermissions(UUID userId, String tenantId, String resourceDomain) {
        String key = buildPermissionsKey(userId, tenantId, resourceDomain);
        
        try {
            Object cachedValue = redisTemplate.opsForValue().get(key);
            if (cachedValue != null) {
                String jsonValue = cachedValue.toString();
                UserPermissionsDto permissions = objectMapper.readValue(jsonValue, UserPermissionsDto.class);
                log.debug("Retrieved cached permissions for user {} in tenant {} and domain {}", userId, tenantId, resourceDomain);
                return permissions;
            }
        } catch (Exception e) {
            log.error("Error retrieving cached permissions for user {}: {}", userId, e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public void evictUserPermissionsCache(UUID userId, String tenantId, String resourceDomain) {
        String key = buildPermissionsKey(userId, tenantId, resourceDomain);
        redisTemplate.delete(key);
        log.debug("Evicted permissions cache for user {} in tenant {} and domain {}", userId, tenantId, resourceDomain);
    }
    
    @Override
    public void evictAllUserPermissionsCache(String tenantId) {
        String pattern = PERMISSIONS_KEY_PREFIX + tenantId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("Evicted all permissions cache for tenant {}", tenantId);
        }
    }
    
    @Override
    public void blacklistJWT(String jti, long expiration) {
        String key = BLACKLIST_KEY_PREFIX + jti;
        long ttl = Math.max(expiration - System.currentTimeMillis(), 0) / 1000;
        
        if (ttl > 0) {
            stringRedisTemplate.opsForValue().set(key, "blacklisted", ttl, TimeUnit.SECONDS);
            log.debug("Blacklisted JWT with jti: {}", jti);
        }
    }
    
    @Override
    public boolean isJWTBlacklisted(String jti) {
        String key = BLACKLIST_KEY_PREFIX + jti;
        Boolean exists = stringRedisTemplate.hasKey(key);
        return exists != null && exists;
    }
    
    @Override
    public void cleanupExpiredJWTs() {
        // Redis automatically handles expiration, so this is a no-op
        // But we can implement custom cleanup logic if needed
        log.debug("JWT cleanup completed (handled by Redis TTL)");
    }
    
    @Override
    public void cacheUserSession(UUID userId, String sessionId, String tenantId, String resourceDomain) {
        String key = buildSessionKey(userId, tenantId, resourceDomain);
        String sessionKey = buildSessionKey(userId, sessionId, tenantId, resourceDomain);
        
        // Add session to user's session set
        redisTemplate.opsForSet().add(key, sessionId);
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
        
        // Cache individual session
        stringRedisTemplate.opsForValue().set(sessionKey, "active", 24, TimeUnit.HOURS);
        
        log.debug("Cached session {} for user {} in tenant {} and domain {}", sessionId, userId, tenantId, resourceDomain);
    }
    
    @Override
    public void evictUserSession(UUID userId, String sessionId, String tenantId, String resourceDomain) {
        String key = buildSessionKey(userId, tenantId, resourceDomain);
        String sessionKey = buildSessionKey(userId, sessionId, tenantId, resourceDomain);
        
        redisTemplate.opsForSet().remove(key, sessionId);
        stringRedisTemplate.delete(sessionKey);
        
        log.debug("Evicted session {} for user {} in tenant {} and domain {}", sessionId, userId, tenantId, resourceDomain);
    }
    
    @Override
    public List<String> getUserSessions(UUID userId, String tenantId, String resourceDomain) {
        String key = buildSessionKey(userId, tenantId, resourceDomain);
        Set<Object> sessions = redisTemplate.opsForSet().members(key);
        
        if (sessions != null) {
            return sessions.stream()
                .map(Object::toString)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        
        return new ArrayList<>();
    }
    
    @Override
    public void evictAllUserSessions(UUID userId, String tenantId, String resourceDomain) {
        String key = buildSessionKey(userId, tenantId, resourceDomain);
        String sessionPattern = SESSION_KEY_PREFIX + tenantId + ":" + resourceDomain + ":" + userId + ":*";
        
        // Remove all individual sessions
        Set<String> sessionKeys = redisTemplate.keys(sessionPattern);
        if (sessionKeys != null && !sessionKeys.isEmpty()) {
            redisTemplate.delete(sessionKeys);
        }
        
        // Remove session set
        redisTemplate.delete(key);
        
        log.debug("Evicted all sessions for user {} in tenant {} and domain {}", userId, tenantId, resourceDomain);
    }
    
    @Override
    public boolean isPermissionCacheAvailable(UUID userId, String tenantId, String resourceDomain) {
        String key = buildPermissionsKey(userId, tenantId, resourceDomain);
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }
    
    @Override
    public long getPermissionCacheTTL(UUID userId, String tenantId, String resourceDomain) {
        String key = buildPermissionsKey(userId, tenantId, resourceDomain);
        Long ttl = redisTemplate.getExpire(key);
        return ttl != null ? ttl : -1;
    }
    
    @Override
    public void clearAllCache() {
        log.warn("Clearing all cache - this operation should be used carefully");
        
        // Clear permissions cache
        Set<String> permissionKeys = redisTemplate.keys(PERMISSIONS_KEY_PREFIX + "*");
        if (permissionKeys != null && !permissionKeys.isEmpty()) {
            redisTemplate.delete(permissionKeys);
        }
        
        // Clear blacklist cache
        Set<String> blacklistKeys = redisTemplate.keys(BLACKLIST_KEY_PREFIX + "*");
        if (blacklistKeys != null && !blacklistKeys.isEmpty()) {
            redisTemplate.delete(blacklistKeys);
        }
        
        // Clear session cache
        Set<String> sessionKeys = redisTemplate.keys(SESSION_KEY_PREFIX + "*");
        if (sessionKeys != null && !sessionKeys.isEmpty()) {
            redisTemplate.delete(sessionKeys);
        }
        
        log.info("All cache cleared");
    }
    
    @Override
    public void clearTenantCache(String tenantId) {
        log.info("Clearing cache for tenant {}", tenantId);
        
        // Clear permissions cache for tenant
        String permissionPattern = PERMISSIONS_KEY_PREFIX + tenantId + ":*";
        Set<String> permissionKeys = redisTemplate.keys(permissionPattern);
        if (permissionKeys != null && !permissionKeys.isEmpty()) {
            redisTemplate.delete(permissionKeys);
        }
        
        // Clear session cache for tenant
        String sessionPattern = SESSION_KEY_PREFIX + tenantId + ":*";
        Set<String> sessionKeys = redisTemplate.keys(sessionPattern);
        if (sessionKeys != null && !sessionKeys.isEmpty()) {
            redisTemplate.delete(sessionKeys);
        }
        
        log.info("Cache cleared for tenant {}", tenantId);
    }
    
    @Override
    public void clearDomainCache(String tenantId, String resourceDomain) {
        log.info("Clearing cache for tenant {} and domain {}", tenantId, resourceDomain);
        
        // Clear permissions cache for tenant and domain
        String permissionPattern = PERMISSIONS_KEY_PREFIX + tenantId + ":" + resourceDomain + ":*";
        Set<String> permissionKeys = redisTemplate.keys(permissionPattern);
        if (permissionKeys != null && !permissionKeys.isEmpty()) {
            redisTemplate.delete(permissionKeys);
        }
        
        // Clear session cache for tenant and domain
        String sessionPattern = SESSION_KEY_PREFIX + tenantId + ":" + resourceDomain + ":*";
        Set<String> sessionKeys = redisTemplate.keys(sessionPattern);
        if (sessionKeys != null && !sessionKeys.isEmpty()) {
            redisTemplate.delete(sessionKeys);
        }
        
        log.info("Cache cleared for tenant {} and domain {}", tenantId, resourceDomain);
    }
    
    // Helper methods
    private String buildPermissionsKey(UUID userId, String tenantId, String resourceDomain) {
        return PERMISSIONS_KEY_PREFIX + tenantId + ":" + resourceDomain + ":" + userId;
    }
    
    private String buildSessionKey(UUID userId, String tenantId, String resourceDomain) {
        return SESSION_KEY_PREFIX + tenantId + ":" + resourceDomain + ":" + userId;
    }
    
    private String buildSessionKey(UUID userId, String sessionId, String tenantId, String resourceDomain) {
        return SESSION_KEY_PREFIX + tenantId + ":" + resourceDomain + ":" + userId + ":" + sessionId;
    }
}
