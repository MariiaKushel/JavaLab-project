package com.epam.esm.details;

import com.epam.esm.dao.entity.User;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserService service;

    @Autowired
    public CustomUserDetailsService(UserService service) {
        this.service = service;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = service.findByUsernameForSecurity(username);
            return new CustomUserDetails(user.getLogin(), user.getPassword(), user.getRole(), user.getId());
        } catch (CustomException e) {
            throw new UsernameNotFoundException("");
        }
    }
}
