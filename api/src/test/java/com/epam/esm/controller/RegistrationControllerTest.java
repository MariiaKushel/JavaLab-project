package com.epam.esm.controller;

import com.epam.esm.config.ApiConfig;
import com.epam.esm.enumeration.UserRole;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.properties.JwtProperty;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.RegistrationFormDto;
import com.epam.esm.service.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrationController.class)
@ContextConfiguration(classes = {ApiConfig.class, TestConfig.class})
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userServiceMock;

    @Autowired
    private JwtProperty jwtProperty;
    @Autowired
    private ObjectMapper mapper;

    private Cookie userJwtCookie;
    private Cookie adminJwtCookie;

    @BeforeEach
    void beforeAll() {
        String userJwtCookieValue = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxQGdtYWlsLmNvbSIsInVzZXJJZCI6MSwicm9sZSI6IlJPTEVfV"
                + "VNFUiJ9.EddlZP2UHYF9kmHURWj-aM9A-Z8e-UMNgle33R_wtH8GKjq7foxvWnWUuIwRuBqwVHEOo1ijVRb-OJDMqTmiTw";
        userJwtCookie = new Cookie(jwtProperty.getCookieName(), userJwtCookieValue);

        String adminJwtCookieValue = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJ1c2VySWQiOjEwMDUsIn"
                + "JvbGUiOiJST0xFX0FETUlOIn0.UAtp-jJQnaAcIji32vtvPssWHSLUFzazIxjf03C_fOgks_i5OPXfaED1naa3zFEVTI"
                + "haAhu9dZ6GBDrh55EyqA";
        adminJwtCookie = new Cookie(jwtProperty.getCookieName(), adminJwtCookieValue);
    }

    @Test
    void createUser_guestWithoutJwtCookieAndCorrectRegistrationForm_ok() throws Exception {
        RegistrationFormDto form = new RegistrationFormDto();
        form.setUsername("42@gmail.com");
        form.setName("Petr");
        form.setPassword("secret");

        UserDto dto = new UserDto();
        dto.setId(42L);
        dto.setUsername("42@gmail.com");
        dto.setName("Petr");
        dto.setRole(UserRole.ROLE_USER);
        Mockito.when(userServiceMock.create(Mockito.any(RegistrationFormDto.class))).thenReturn(dto);

        String jsonContent = mapper.writeValueAsString(form);

        mockMvc.perform(post("/registration")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.username").value("42@gmail.com"))
                .andExpect(jsonPath("$.name").value("Petr"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));

        Mockito.verify(userServiceMock, Mockito.times(1))
                .create(Mockito.any(RegistrationFormDto.class));
    }

    @Test
    void createUser_guestWithoutJwtCookieAndNotValidUsername_bagRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(userServiceMock.create(Mockito.any())).thenThrow(ex);

        RegistrationFormDto form = new RegistrationFormDto();
        form.setUsername("wrong username");
        form.setName("Petr");
        form.setPassword("secret");
        String jsonContent = mapper.writeValueAsString(form);

        mockMvc.perform(post("/registration")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(userServiceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void createUser_guestWithoutJwtCookieAndNotPasswordOrName_bagRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(userServiceMock.create(Mockito.any())).thenThrow(ex);

        RegistrationFormDto form = new RegistrationFormDto();
        form.setUsername("42@gmail.com");
        form.setName("wrong name");
        form.setPassword("wrong password");
        String jsonContent = mapper.writeValueAsString(form);

        mockMvc.perform(post("/registration")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(userServiceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void createUser_guestWithoutJwtCookieAndExistentUserWithThatUsername_conflict() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_ALREADY_EXIST);
        Mockito.when(userServiceMock.create(Mockito.any())).thenThrow(ex);

        RegistrationFormDto form = new RegistrationFormDto();
        form.setUsername("42@gmail.com");
        form.setName("Petr");
        form.setPassword("secret");
        String jsonContent = mapper.writeValueAsString(form);

        mockMvc.perform(post("/registration")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value("Resource already exist: error"))
                .andExpect(jsonPath("$.errorCode").value(40901));

        Mockito.verify(userServiceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void createUser_userJwtCookie_forbidden() throws Exception {
        mockMvc.perform(post("/registration")
                        .cookie(userJwtCookie)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void createUser_adminJwtCookie_forbidden() throws Exception {
        mockMvc.perform(post("/registration")
                        .cookie(adminJwtCookie)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}