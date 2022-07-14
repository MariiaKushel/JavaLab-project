package com.epam.esm.details;

import com.epam.esm.dao.entity.User;
import com.epam.esm.enumeration.UserRole;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class CustomUserDetailsServiceTest {

    UserService userServiceMock;
    CustomUserDetailsService service;

    public CustomUserDetailsServiceTest (){
        userServiceMock = Mockito.mock(UserService.class);
        service = new CustomUserDetailsService(userServiceMock);
    }

    @Test
    void loadUserByUsername() throws CustomException {
        User user = new User();
        user.setId(1L);
        user.setLogin("ivan@gmail.com");
        user.setPassword("ivan_password");
        user.setName("Ivan");
        user.setRole(UserRole.ROLE_USER);
        Mockito.when(userServiceMock.findByUsernameForSecurity(Mockito.anyString())).thenReturn(user);
        UserDetails expected = new CustomUserDetails("ivan@gmail.com", "ivan_password",
                UserRole.ROLE_USER, 1L);
        UserDetails actual = service.loadUserByUsername("ivan@gmail.com");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void loadUserByUsernameNotFoundException() throws CustomException {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.when(userServiceMock.findByUsernameForSecurity(Mockito.anyString())).thenThrow(ex);
        UsernameNotFoundException e = Assertions.assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("wrong_username@gmail.com"));
        String actual = e.getMessage();
        String expected = "";
        Assertions.assertEquals(expected, actual);
    }
}