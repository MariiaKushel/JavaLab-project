package com.epam.esm.details;

import com.epam.esm.enumeration.UserRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Set;

public class CustomUserDetails extends User {
    private Long userId;
    private UserRole role;

    public CustomUserDetails(String username, String password, UserRole role, Long userId) {
        super(username,
                password,
                true,
                true,
                true,
                true,
                Set.of(new SimpleGrantedAuthority(role.name())));
        this.role = role;
        this.userId = userId;
    }

    public String getRoleJWT() {
        return this.role.name();
    }

    public Long getUserId() {
        return this.userId;
    }
}
