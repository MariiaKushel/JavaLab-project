package com.epam.esm.controller;

import com.epam.esm.config.ApiConfig;
import com.epam.esm.dao.entity.User;
import com.epam.esm.enumeration.UserRole;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.properties.JwtProperty;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.UserDto;
import com.epam.esm.util.impl.AdminCollectionLinkCreator;
import com.epam.esm.util.impl.AdminSingleEntityLinkCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@ContextConfiguration(classes = {ApiConfig.class, TestConfig.class})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userServiceMock;
    @MockBean
    private OrderService orderServiceMock;
    @Autowired
    private AdminSingleEntityLinkCreator singleEntityLinkCreator;
    @Autowired
    private AdminCollectionLinkCreator collectionLinkCreator;
    @Autowired
    private JwtProperty jwtProperty;

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
        existentUser.setRole(UserRole.ROLE_USER);

        existentAdmin = new User();
        existentAdmin.setId(1001L);
        existentAdmin.setLogin("admin@gmail.com");
        existentAdmin.setPassword("$10$8AqdT3Ks6YjuEFUlA9Y2feE/j/zHRUohmKaDu8xvu/j3V4EEnyLZO");
        existentAdmin.setName("Vasiay");
        existentAdmin.setRole(UserRole.ROLE_ADMIN);

        String userJwtCookieValue = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxQGdtYWlsLmNvbSIsInVzZXJJZCI6MSwicm9sZSI6IlJPTEVfV"
                + "VNFUiJ9.EddlZP2UHYF9kmHURWj-aM9A-Z8e-UMNgle33R_wtH8GKjq7foxvWnWUuIwRuBqwVHEOo1ijVRb-OJDMqTmiTw";
        userJwtCookie = new Cookie(jwtProperty.getCookieName(), userJwtCookieValue);

        String adminJwtCookieValue = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJ1c2VySWQiOjEwMDUsIn"
                + "JvbGUiOiJST0xFX0FETUlOIn0.UAtp-jJQnaAcIji32vtvPssWHSLUFzazIxjf03C_fOgks_i5OPXfaED1naa3zFEVTI"
                + "haAhu9dZ6GBDrh55EyqA";
        adminJwtCookie = new Cookie(jwtProperty.getCookieName(), adminJwtCookieValue);
    }

    @Test
    void findAdmin_correctAdminJWTCookie_ok() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1001L);
        userDto.setName("admin@gmail.com");
        userDto.setName("Vasiay");
        userDto.setRole(UserRole.ROLE_ADMIN);
        Mockito.when(userServiceMock.findById(Mockito.anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/admin")
                        .cookie(adminJwtCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1001))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.orders.href", notNullValue()));

        Mockito.verify(userServiceMock, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void findAdmin_userJWTCookie_forbidden() throws Exception {
        mockMvc.perform(get("/admin")
                        .cookie(userJwtCookie))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void findAdmin_guestWithoutJwtCookie_unauthorized() throws Exception {
        mockMvc.perform(get("/admin"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findOrdersByUser_correctAdminJWTCookieAndPaginationParameters_ok() throws Exception {
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

        mockMvc.perform(get("/admin/users/1/orders?page=2&size=3")
                        .cookie(adminJwtCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.orders", hasSize(3)))
                .andExpect(jsonPath("$._links.firstPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.currentPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.lastPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.admin.href", notNullValue()));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findAllByUserLastPage(Mockito.anyLong(), Mockito.anyInt());
    }

    @Test
    void findOrdersByUser_userJWTCookie_forbidden() throws Exception {
        mockMvc.perform(get("/admin/users/1/orders?page=2&size=3")
                        .cookie(userJwtCookie))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void findOrdersByUser_guestWithoutJwtCookie_unauthorized() throws Exception {
        mockMvc.perform(get("/admin/users/1/orders?page=2&size=3"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findOrdersByUser_notValidPaginationParameters_badRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(orderServiceMock.findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(ex);

        mockMvc.perform(get("/admin/users/1/orders?page=-2&size=-3")
                        .cookie(adminJwtCookie))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void findOrdersByUser_notValidUserId_badRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(orderServiceMock.findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(ex);

        mockMvc.perform(get("/admin/users/-1/orders?page=2&size=3")
                        .cookie(adminJwtCookie))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void findOrdersByUser_nonExistentUser_notFound() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.when(orderServiceMock.findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(ex);

        mockMvc.perform(get("/admin/users/999999/orders?page=2&size=3")
                        .cookie(adminJwtCookie))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void findOrder_correctAdminJWTCookieAndExistentUserIdAndOrderId_ok() throws Exception {
        OrderDto order = new OrderDto();
        order.setId(1L);
        Mockito.when(orderServiceMock.findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong())).thenReturn(order);

        mockMvc.perform(get("/admin/users/1/orders/42")
                        .cookie(adminJwtCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.orders.href", notNullValue()))
                .andExpect(jsonPath("$._links.admin.href", notNullValue()));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void findOrder_userJWTCookie_forbidden() throws Exception {
        mockMvc.perform(get("/admin/users/1/orders/42")
                        .cookie(userJwtCookie))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void findOrder_guestWithoutJwtCookie_unauthorized() throws Exception {
        mockMvc.perform(get("/admin/users/1/orders/42"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findOrder_NotValidOrderId_badRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(orderServiceMock.findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong())).thenThrow(ex);

        mockMvc.perform(get("/admin/users/1/orders/-42")
                        .cookie(adminJwtCookie))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void findOrder_NotValidUserId_badRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(orderServiceMock.findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong())).thenThrow(ex);

        mockMvc.perform(get("/admin/users/-1/orders/42")
                        .cookie(adminJwtCookie))
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

        mockMvc.perform(get("/admin/users/1/orders/1")
                        .cookie(adminJwtCookie))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong());
    }
}