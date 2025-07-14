package service.com.service.userservice.utils;

public class TenantContext {
    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> DOMAIN_ID = new ThreadLocal<>();
    
    public static void setTenantId(String tenantId) {
        TENANT_ID.set(tenantId);
    }
    
    public static String getTenantId() {
        return TENANT_ID.get();
    }
    
    public static void setDomainId(String domainId) {
        DOMAIN_ID.set(domainId);
    }
    
    public static String getDomainId() {
        return DOMAIN_ID.get();
    }
    
    public static void clear() {
        TENANT_ID.remove();
        DOMAIN_ID.remove();
    }
}
