package com.epam.esm.controller;

import com.epam.esm.config.ApiConfig;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {ApiConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userServiceMock;
    @MockBean
    private OrderService orderServiceMock;

    @Test
    void findUser() throws Exception {
        UserDto user = new UserDto();
        user.setId(1L);
        Mockito.when(userServiceMock.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.orders.href", notNullValue()));

        Mockito.verify(userServiceMock, Mockito.times(1)).findById(1L);
    }

    @Test
    void findTagNotValidDataException() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(userServiceMock.findById(-1L)).thenThrow(ex);

        mockMvc.perform(get("/users/-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(userServiceMock, Mockito.times(1)).findById(-1L);
    }

    @Test
    void findTagNotFoundException() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.when(userServiceMock.findById(999L)).thenThrow(ex);

        mockMvc.perform(get("/users/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(userServiceMock, Mockito.times(1)).findById(999L);
    }

    @Test
    void findOrders() throws Exception {
        OrderDto order1 = new OrderDto();
        order1.setId(1L);
        OrderDto order2 = new OrderDto();
        order2.setId(2L);
        OrderDto order3 = new OrderDto();
        order3.setId(3L);
        List<OrderDto> orders = List.of(order1, order2, order3);

        Mockito.when(orderServiceMock.findAllByUser(1L, 2, 3)).thenReturn(orders);
        Mockito.when(orderServiceMock.countByUser(Mockito.anyLong())).thenReturn(100L);

        mockMvc.perform(get("/users/1/orders?page=2&size=3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.orderDtoList", hasSize(3)))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()));

        Mockito.verify(orderServiceMock, Mockito.times(1)).findAllByUser(1L, 2, 3);
        Mockito.verify(orderServiceMock, Mockito.times(1)).countByUser(Mockito.anyLong());
    }

    @Test
    void findOrder() throws Exception {
        OrderDto order = new OrderDto();
        order.setId(1L);
        Mockito.when(orderServiceMock.findByIdAndByUser(1L, 1L)).thenReturn(order);

        mockMvc.perform(get("/users/1/orders/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.order.href", notNullValue()));

        Mockito.verify(orderServiceMock, Mockito.times(1)).findByIdAndByUser(1L, 1L);
    }

    @Test
    void findOrderNotValidDataException() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(orderServiceMock.findByIdAndByUser(-1L, -1L)).thenThrow(ex);

        mockMvc.perform(get("/users/-1/orders/-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(orderServiceMock, Mockito.times(1)).findByIdAndByUser(-1L, -1L);
    }

    @Test
    void findOrderNotFoundException() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.when(orderServiceMock.findByIdAndByUser(1L, 1L)).thenThrow(ex);

        mockMvc.perform(get("/users/1/orders/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(orderServiceMock, Mockito.times(1)).findByIdAndByUser(1L, 1L);
    }


    @Test
    void createOrder() throws Exception {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        CertificateDto dto2 = new CertificateDto();
        dto2.setId(1L);
        CertificateDto dto3 = new CertificateDto();
        dto3.setId(3L);
        List<CertificateDto> dtos = List.of(dto1, dto2, dto3);
        OrderDto order = new OrderDto();
        order.setId(42L);
        order.setAmount(new BigDecimal("333.33"));
        Mockito.when(orderServiceMock.create(1L, dtos)).thenReturn(order);

        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(dtos);

        mockMvc.perform(post("/users/1/orders/")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.amount").value(333.33))
                .andExpect(jsonPath("$._links.order.href", notNullValue()));

        Mockito.verify(orderServiceMock, Mockito.times(1)).create(1L, dtos);
    }
}