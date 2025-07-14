package service.com.service.userservice.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@UtilityClass
@Slf4j
public class ValidationUtils {
    
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,50}$";
    
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    
    private static final Pattern EMAIL_COMPILED_PATTERN = Pattern.compile(EMAIL_PATTERN);
    private static final Pattern USERNAME_COMPILED_PATTERN = Pattern.compile(USERNAME_PATTERN);
    private static final Pattern PASSWORD_COMPILED_PATTERN = Pattern.compile(PASSWORD_PATTERN);
    
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_COMPILED_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_COMPILED_PATTERN.matcher(username).matches();
    }
    
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return PASSWORD_COMPILED_PATTERN.matcher(password).matches();
    }
    
    public static boolean isValidTenantId(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            return false;
        }
        return tenantId.length() >= 1 && tenantId.length() <= 255;
    }
    
    public static boolean isValidResourceDomain(String resourceDomain) {
        if (resourceDomain == null || resourceDomain.trim().isEmpty()) {
            return false;
        }
        return resourceDomain.length() >= 1 && resourceDomain.length() <= 255;
    }
    
    public static boolean isValidName(String name) {
        if (name == null) {
            return true; // Names can be null
        }
        return name.trim().length() <= 100;
    }
    
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim();
    }
    
    public static boolean isValidPermissionFormat(String permission) {
        if (permission == null || permission.trim().isEmpty()) {
            return false;
        }
        // Permission format: resource:action
        String[] parts = permission.split(":");
        return parts.length == 2 && 
               !parts[0].trim().isEmpty() && 
               !parts[1].trim().isEmpty();
    }
    
    public static boolean isValidRoleName(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            return false;
        }
        return roleName.length() >= 1 && roleName.length() <= 255;
    }
}
