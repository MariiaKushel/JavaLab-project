package com.epam.esm.security;

import com.epam.esm.dao.entity.User;
import com.epam.esm.enumeration.UserRole;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Class represent custom user details service.
 */
@Service
public class SecurityService implements UserDetailsService {

    private UserService service;

    @Autowired
    public SecurityService(UserService service) {
        this.service = service;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = service.findByUsernameForSecurity(username);
            UserRole role = UserRole.valueOf(user.getRole().getName());
            return new CustomUserDetails(user.getLogin(), user.getPassword(), role, user.getId());
        } catch (CustomException e) {
            throw new UsernameNotFoundException("");
        }
    }
}
