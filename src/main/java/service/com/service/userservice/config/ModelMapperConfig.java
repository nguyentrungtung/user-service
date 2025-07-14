package service.com.service.userservice.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import service.com.service.userservice.dto.UserDto;
import service.com.service.userservice.dto.RoleDto;
import service.com.service.userservice.dto.PermissionDto;
import service.com.service.userservice.entity.User;
import service.com.service.userservice.entity.Role;
import service.com.service.userservice.entity.Permission;

@Configuration
public class ModelMapperConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        
        // Configure mappings
        configureUserMapping(mapper);
        configureRoleMapping(mapper);
        configurePermissionMapping(mapper);
        
        return mapper;
    }
    
    private void configureUserMapping(ModelMapper mapper) {
        // User to UserDto mapping
        mapper.createTypeMap(User.class, UserDto.class)
            .addMappings(mapping -> {
                mapping.skip(UserDto::setRoles);
                mapping.skip(UserDto::setPermissions);
            });
        
        // UserDto to User mapping
        mapper.createTypeMap(UserDto.class, User.class)
            .addMappings(mapping -> {
                mapping.skip(User::setRoles);
                mapping.skip(User::setPermissions);
            });
    }
    
    private void configureRoleMapping(ModelMapper mapper) {
        // Role to RoleDto mapping
        mapper.createTypeMap(Role.class, RoleDto.class)
            .addMappings(mapping -> {
                mapping.skip(RoleDto::setPermissions);
            });
        
        // RoleDto to Role mapping
        mapper.createTypeMap(RoleDto.class, Role.class)
            .addMappings(mapping -> {
                mapping.skip(Role::setPermissions);
                mapping.skip(Role::setUsers);
            });
    }
    
    private void configurePermissionMapping(ModelMapper mapper) {
        // Permission to PermissionDto mapping is straightforward
        mapper.createTypeMap(Permission.class, PermissionDto.class);
        
        // PermissionDto to Permission mapping
        mapper.createTypeMap(PermissionDto.class, Permission.class)
            .addMappings(mapping -> {
                mapping.skip(Permission::setRoles);
                mapping.skip(Permission::setUsers);
            });
    }
}
