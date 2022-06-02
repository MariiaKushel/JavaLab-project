package com.epam.esm.controller;

import com.epam.esm.config.ApiConfig;
import com.epam.esm.dao.entity.Role;
import com.epam.esm.dao.entity.User;
import com.epam.esm.enumeration.AppRole;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.properties.JwtProperty;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.UserDto;
import com.epam.esm.util.impl.UserCollectionLinkCreator;
import com.epam.esm.util.impl.UserSingleEntityLinkCreator;
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
import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {ApiConfig.class, TestConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userServiceMock;
    @MockBean
    private OrderService orderServiceMock;
    @Autowired
    private UserSingleEntityLinkCreator singleEntityLinkCreator;
    @Autowired
    private UserCollectionLinkCreator collectionLinkCreator;
    @Autowired
    private JwtProperty jwtProperty;
    @Autowired
    private ObjectMapper mapper;

    private User existentUser;
    private User existentAdmin;
    private Cookie userJwtCookie;
    private Cookie adminJwtCookie;

    @BeforeEach
    void beforeAll() {
        existentUser = new User();
        existentUser.setId(1L);
        existentUser.setLogin("1@gmail.com");
        existentUser.setPassword("$2a$10$/eDeS0UCI/TDHdkHBBgYtOVlrTR.P6.uYnERH7z4jcMCvTyXKM7MS");
        existentUser.setName("Petr");
        Role roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName("ROLE_USER");
        existentUser.setRole(roleUser);

        existentAdmin = new User();
        existentAdmin.setId(1001L);
        existentAdmin.setLogin("admin@gmail.com");
        existentAdmin.setPassword("$10$8AqdT3Ks6YjuEFUlA9Y2feE/j/zHRUohmKaDu8xvu/j3V4EEnyLZO");
        existentAdmin.setName("Vasiay");
        Role roleAdmin = new Role();
        roleAdmin.setId(2L);
        roleAdmin.setName("ROLE_ADMIN");
        existentAdmin.setRole(roleAdmin);

        String userJwtCookieValue = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxQGdtYWlsLmNvbSIsInVzZXJJZCI6MSwicm9sZSI6IlJPTEVfV"
                + "VNFUiJ9.EddlZP2UHYF9kmHURWj-aM9A-Z8e-UMNgle33R_wtH8GKjq7foxvWnWUuIwRuBqwVHEOo1ijVRb-OJDMqTmiTw";
        userJwtCookie = new Cookie(jwtProperty.getCookieName(), userJwtCookieValue);

        String adminJwtCookieValue = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJ1c2VySWQiOjEwMDUsIn"
                + "JvbGUiOiJST0xFX0FETUlOIn0.UAtp-jJQnaAcIji32vtvPssWHSLUFzazIxjf03C_fOgks_i5OPXfaED1naa3zFEVTI"
                + "haAhu9dZ6GBDrh55EyqA";
        adminJwtCookie = new Cookie(jwtProperty.getCookieName(), adminJwtCookieValue);
    }

    @Test
    void findUser_correctUserJWTCookie_ok() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("1@gmail.com");
        userDto.setName("Petr");
        userDto.setRole(AppRole.ROLE_USER);
        Mockito.when(userServiceMock.findById(Mockito.anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/user")
                        .cookie(userJwtCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.orders.href", notNullValue()))
                .andExpect(jsonPath("$._links.createOrder.href", notNullValue()));

        Mockito.verify(userServiceMock, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void findUser_adminJWTCookie_forbidden() throws Exception {
        mockMvc.perform(get("/user")
                        .cookie(adminJwtCookie))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void findUser_guestWithoutJwtCookie_unauthorized() throws Exception {
        mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findOrders_correctUserJWTCookieAndPaginationParameters_ok() throws Exception {
        OrderDto order1 = new OrderDto();
        order1.setId(1L);
        OrderDto order2 = new OrderDto();
        order2.setId(2L);
        OrderDto order3 = new OrderDto();
        order3.setId(3L);
        List<OrderDto> orders = List.of(order1, order2, order3);

        Mockito.when(orderServiceMock.findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(orders);
        Mockito.when(orderServiceMock.findAllByUserLastPage(Mockito.anyLong(), Mockito.anyInt()))
                .thenReturn(100);

        mockMvc.perform(get("/user/orders?page=2&size=3")
                        .cookie(userJwtCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.orders", hasSize(3)))
                .andExpect(jsonPath("$._links.firstPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.currentPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.lastPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.createOrder.href", notNullValue()))
                .andExpect(jsonPath("$._links.user.href", notNullValue()));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findAllByUserLastPage(Mockito.anyLong(), Mockito.anyInt());
    }

    @Test
    void findOrders_adminJWTCookie_forbidden() throws Exception {
        mockMvc.perform(get("/user/orders?page=2&size=3")
                        .cookie(adminJwtCookie))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void findOrders_guestWithoutJwtCookie_unauthorized() throws Exception {
        mockMvc.perform(get("/user/orders?page=2&size=3"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findOrders_notValidPaginationParameters_badRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(orderServiceMock.findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(ex);

        mockMvc.perform(get("/user/orders?page=-2&size=-3")
                        .cookie(userJwtCookie))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void findOrder_correctUserJWTCookieAndExistentOrderId_ok() throws Exception {
        OrderDto order = new OrderDto();
        order.setId(1L);
        Mockito.when(orderServiceMock.findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong())).thenReturn(order);

        mockMvc.perform(get("/user/orders/1")
                        .cookie(userJwtCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.orders.href", notNullValue()))
                .andExpect(jsonPath("$._links.createOrder.href", notNullValue()))
                .andExpect(jsonPath("$._links.user.href", notNullValue()));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void findOrder_adminJWTCookie_forbidden() throws Exception {
        mockMvc.perform(get("/user/orders/1")
                        .cookie(adminJwtCookie))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void findOrder_guestWithoutJwtCookie_unauthorized() throws Exception {
        mockMvc.perform(get("/user/orders/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findOrder_NotValidOrderId_badRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(orderServiceMock.findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong())).thenThrow(ex);

        mockMvc.perform(get("/user/orders/-1")
                        .cookie(userJwtCookie))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void findOrder_nonExistentOrderByUser_notFound() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.when(orderServiceMock.findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong())).thenThrow(ex);

        mockMvc.perform(get("/user/orders/1")
                        .cookie(userJwtCookie))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void createOrder_correctUserJWTCookieAndCertificateList_ok() throws Exception {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        dto1.setPrice(new BigDecimal("100.00"));
        CertificateDto dto2 = new CertificateDto();
        dto2.setId(1L);
        dto2.setPrice(new BigDecimal("110.00"));
        CertificateDto dto3 = new CertificateDto();
        dto3.setId(3L);
        dto3.setPrice(new BigDecimal("123.33"));
        List<CertificateDto> dtos = List.of(dto1, dto2, dto3);
        OrderDto order = new OrderDto();
        order.setId(42L);
        order.setAmount(new BigDecimal("333.33"));
        Mockito.when(orderServiceMock.create(Mockito.anyLong(), Mockito.anyList())).thenReturn(order);

        String jsonContent = mapper.writeValueAsString(dtos);

        mockMvc.perform(post("/user/orders")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .cookie(userJwtCookie)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.amount").value(333.33))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.orders.href", notNullValue()))
                .andExpect(jsonPath("$._links.createOrder.href", notNullValue()))
                .andExpect(jsonPath("$._links.user.href", notNullValue()));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .create(Mockito.anyLong(), Mockito.anyList());
    }

    @Test
    void createOrder_adminJWTCookie_forbidden() throws Exception {
        mockMvc.perform(post("/user/orders")
                        .cookie(adminJwtCookie)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void createOrder_guestWithoutJwtCookie_unauthorized() throws Exception {
        mockMvc.perform(post("/user/orders")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createOrder_notValidCertificateList_badRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(orderServiceMock.create(Mockito.anyLong(), Mockito.anyList())).thenThrow(ex);

        List<CertificateDto> dtos = List.of(new CertificateDto());
        String jsonContent = mapper.writeValueAsString(dtos);

        mockMvc.perform(post("/user/orders")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .cookie(userJwtCookie)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .create(Mockito.anyLong(), Mockito.anyList());
    }

    @Test
    void createOrder_nonExistentCertificate_notFound() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.when(orderServiceMock.create(Mockito.anyLong(), Mockito.anyList())).thenThrow(ex);

        List<CertificateDto> dtos = List.of(new CertificateDto());
        String jsonContent = mapper.writeValueAsString(dtos);

        mockMvc.perform(post("/user/orders")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .cookie(userJwtCookie)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .create(Mockito.anyLong(), Mockito.anyList());
    }
}