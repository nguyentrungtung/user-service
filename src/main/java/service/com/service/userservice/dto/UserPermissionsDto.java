package service.com.service.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionsDto {
    
    private UUID userId;
    private String username;
    private String tenantId;
    private String resourceDomain;
    private List<String> permissions;
    private List<String> roles;
    
    // Helper method to check if user has permission
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
    
    // Helper method to check if user has role
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
}
