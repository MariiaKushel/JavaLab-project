package com.epam.esm.controller;

import com.epam.esm.ResourceServerApplication;
import com.epam.esm.enumeration.UserRole;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.RegistrationFormDto;
import com.epam.esm.service.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrationController.class)
@ContextConfiguration(classes = {ResourceServerApplication.class, TestConfig.class})
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userServiceMock;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private JwtGrantedAuthoritiesConverter customConverter;

    private Jwt userJwt;
    private Jwt adminJwt;

    @BeforeEach
    void setUp() {
        userJwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("scope", "all")
                .claim("user_id", Long.valueOf(1L))
                .claim("user_name", "1@gmail.com")
                .claim("authorities", new JSONArray().appendElement("ROLE_USER"))
                .build();
        adminJwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("scope", "all")
                .claim("user_id", Long.valueOf(1001L))
                .claim("user_name", "admin@gmail.com")
                .claim("authorities", new JSONArray().appendElement("ROLE_ADMIN"))
                .build();
    }

    @Test
    void createUser_guestWithoutJwtAndCorrectRegistrationForm_ok() throws Exception {
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
    void createUser_guestWithoutJwtAndNotValidUsername_bagRequest() throws Exception {
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
    void createUser_guestWithoutJwtAndNotPasswordOrName_bagRequest() throws Exception {
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
    void createUser_guestWithoutJwtAndExistentUserWithThatUsername_conflict() throws Exception {
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
    void createUser_userJwt_forbidden() throws Exception {
        RegistrationFormDto form = new RegistrationFormDto();
        form.setUsername("42@gmail.com");
        form.setName("Petr");
        form.setPassword("secret");

        String jsonContent = mapper.writeValueAsString(form);

        mockMvc.perform(post("/registration")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void createUser_adminJwt_forbidden() throws Exception {
        RegistrationFormDto form = new RegistrationFormDto();
        form.setUsername("42@gmail.com");
        form.setName("Petr");
        form.setPassword("secret");

        String jsonContent = mapper.writeValueAsString(form);
        mockMvc.perform(post("/registration")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}