package service.com.service.userservice.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import service.com.service.userservice.utils.TenantContext;

@Component
@Slf4j
public class TenantContextInterceptor implements HandlerInterceptor {
    
    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String DOMAIN_HEADER = "X-Domain-ID";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tenantId = request.getHeader(TENANT_HEADER);
        String domainId = request.getHeader(DOMAIN_HEADER);
        
        // For actuator endpoints, we don't need tenant context
        if (request.getRequestURI().startsWith("/actuator")) {
            return true;
        }
        
        // For API endpoints, tenant context is optional for now (to allow testing)
        if (tenantId != null) {
            TenantContext.setTenantId(tenantId);
            log.debug("Set tenant context: {}", tenantId);
        }
        
        if (domainId != null) {
            TenantContext.setDomainId(domainId);
            log.debug("Set domain context: {}", domainId);
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Clear tenant context after request processing
        TenantContext.clear();
        log.debug("Cleared tenant context");
    }
}
