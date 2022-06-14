package com.epam.esm.controller;

import com.epam.esm.ResourceServerApplication;
import com.epam.esm.enumeration.UserRole;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {ResourceServerApplication.class, TestConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userServiceMock;
    @MockBean
    private OrderService orderServiceMock;
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
                .claim("authorities", "ROLE_USER")
                .build();
        adminJwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("scope", "all")
                .claim("user_id", Long.valueOf(1001L))
                .claim("user_name", "admin@gmail.com")
                .claim("authorities", "ROLE_ADMIN")
                .build();
    }

    @Test
    void findUser_correctUserJWT_ok() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("1@gmail.com");
        userDto.setName("Petr");
        userDto.setRole(UserRole.ROLE_USER);
        Mockito.when(userServiceMock.findById(Mockito.anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/users/me")
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.orders.href", notNullValue()))
                .andExpect(jsonPath("$._links.createOrder.href", notNullValue()));

        Mockito.verify(userServiceMock, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void findUser_correctAdminJWT_ok() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1001L);
        userDto.setUsername("admin@gmail.com");
        userDto.setName("PetrAdmin");
        userDto.setRole(UserRole.ROLE_ADMIN);
        Mockito.when(userServiceMock.findById(Mockito.anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/users/me")
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1001))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.orders.href", notNullValue()));

        Mockito.verify(userServiceMock, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void findUser_guestWithoutJwt_unauthorized() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findOrdersByCurrentUser_correctUserJWTAndPaginationParameters_ok() throws Exception {
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

        mockMvc.perform(get("/users/me/orders?page=2&size=3")
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
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
    void findOrdersByCurrentUser_adminJWT_forbidden() throws Exception {
        mockMvc.perform(get("/users/me/orders?page=2&size=3")
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void findOrdersByCurrentUser_guestWithoutJwt_unauthorized() throws Exception {
        mockMvc.perform(get("/users/me/orders?page=2&size=3"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findOrdersByCurrentUser_notValidPaginationParameters_badRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(orderServiceMock.findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(ex);

        mockMvc.perform(get("/users/me/orders?page=-2&size=-3")
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void findOrdersByUser_correctAdminJWTAndPaginationParameters_ok() throws Exception {
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

        mockMvc.perform(get("/users/1/orders?page=2&size=3")
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.orders", hasSize(3)))
                .andExpect(jsonPath("$._links.firstPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.currentPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.lastPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.user.href", notNullValue()));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findAllByUserLastPage(Mockito.anyLong(), Mockito.anyInt());
    }

    @Test
    void findOrdersByUser_userJWT_forbidden() throws Exception {
        mockMvc.perform(get("/users/1/orders?page=2&size=3")
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void findOrdersByUser_guestWithoutJwt_unauthorized() throws Exception {
        mockMvc.perform(get("/users/1/orders?page=2&size=3"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findOrdersByUser_notValidPaginationParameters_badRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(orderServiceMock.findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(ex);

        mockMvc.perform(get("/users/1/orders?page=-2&size=-3")
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findAllByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void findOrderByCurrentUser_correctUserJWTAndExistentOrderId_ok() throws Exception {
        OrderDto order = new OrderDto();
        order.setId(1L);
        Mockito.when(orderServiceMock.findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong())).thenReturn(order);

        mockMvc.perform(get("/users/me/orders/1")
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
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
    void findOrderByCurrentUser_adminJWT_forbidden() throws Exception {
        mockMvc.perform(get("/users/me/orders/1")
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void findOrderByCurrentUser_guestWithoutJwt_unauthorized() throws Exception {
        mockMvc.perform(get("/users/me/orders/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findOrderByCurrentUser_NotValidOrderId_badRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(orderServiceMock.findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong())).thenThrow(ex);

        mockMvc.perform(get("/users/me/orders/-1")
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void findOrderByCurrentUser_nonExistentOrderByUser_notFound() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.when(orderServiceMock.findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong())).thenThrow(ex);

        mockMvc.perform(get("/users/me/orders/1")
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void findOrderByUser_correctAdminJWTAndExistentOrderId_ok() throws Exception {
        OrderDto order = new OrderDto();
        order.setId(1L);
        Mockito.when(orderServiceMock.findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong())).thenReturn(order);

        mockMvc.perform(get("/users/1/orders/1")
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.orders.href", notNullValue()))
                .andExpect(jsonPath("$._links.user.href", notNullValue()));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void findOrderByUser_userJWT_forbidden() throws Exception {
        mockMvc.perform(get("/users/1/orders/1")
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void findOrderByUser_guestWithoutJwt_unauthorized() throws Exception {
        mockMvc.perform(get("/users/1/orders/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findOrderByUser_NotValidOrderId_badRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(orderServiceMock.findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong())).thenThrow(ex);

        mockMvc.perform(get("/users/1/orders/-1")
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void findOrderByUser_nonExistentOrderByUser_notFound() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.when(orderServiceMock.findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong())).thenThrow(ex);

        mockMvc.perform(get("/users/1/orders/1")
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void createOrder_correctUserJWTAndCertificateList_ok() throws Exception {
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

        mockMvc.perform(post("/users/me/orders")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
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
    void createOrder_adminJWT_forbidden() throws Exception {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        dto1.setPrice(new BigDecimal("100.00"));
        List<CertificateDto> dtos = List.of(dto1);

        String jsonContent = mapper.writeValueAsString(dtos);

        mockMvc.perform(post("/users/me/orders")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void createOrder_guestWithoutJwt_unauthorized() throws Exception {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        dto1.setPrice(new BigDecimal("100.00"));
        List<CertificateDto> dtos = List.of(dto1);

        String jsonContent = mapper.writeValueAsString(dtos);

        mockMvc.perform(post("/users/me/orders")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
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

        mockMvc.perform(post("/users/me/orders")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
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

        mockMvc.perform(post("/users/me/orders")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(orderServiceMock, Mockito.times(1))
                .create(Mockito.anyLong(), Mockito.anyList());
    }
}