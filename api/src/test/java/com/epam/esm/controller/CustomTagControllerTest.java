package com.epam.esm.controller;

import com.epam.esm.config.WebConfig;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.CustomTagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WebConfig.class, TestContext.class})
@WebAppConfiguration
class CustomTagControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private CustomTagService serviceMock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        Mockito.reset(serviceMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void findCustomTag() throws Exception {
        CustomTag tag = new CustomTag(1L, "tag");
        Mockito.when(serviceMock.findById(1L)).thenReturn(Optional.of(tag));

        mockMvc.perform(get("/custom-tags/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entityId").value(1))
                .andExpect(jsonPath("$.name").value("tag"));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(1L);
    }

    @Test
    void findCustomTagExceptionNotValidData() throws Exception {
        CustomException ex = new CustomException("Not valid", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.findById(-1L)).thenThrow(ex);

        mockMvc.perform(get("/custom-tags/-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(-1L);
    }

    @Test
    void findCustomTagExceptionResourceNotFound() throws Exception {
        Mockito.when(serviceMock.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/custom-tags/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(999L);
    }

    @Test
    void findAllCustomTag() throws Exception {
        CustomTag tag1 = new CustomTag(1L, "tag1");
        CustomTag tag2 = new CustomTag(2L, "tag2");
        CustomTag tag3 = new CustomTag(3L, "tag3");
        List<CustomTag> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);
        tags.add(tag3);
        Mockito.when(serviceMock.findAll()).thenReturn(tags);

        mockMvc.perform(get("/custom-tags"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll();
    }

    @Test
    void deleteCustomTag() throws Exception {
        Mockito.doNothing().when(serviceMock).delete(Mockito.anyLong());

        mockMvc.perform(delete("/custom-tags/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Mockito.verify(serviceMock, Mockito.times(1)).delete(Mockito.anyLong());
    }

    @Test
    void deleteCustomTagExceptionNotValidData() throws Exception {
        CustomException ex = new CustomException("Not valid", CustomErrorCode.NOT_VALID_DATA);
        Mockito.doThrow(ex).when(serviceMock).delete(-1L);

        mockMvc.perform(delete("/custom-tags/-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).delete(-1L);
    }

    @Test
    void createCustomTag() throws Exception {
        CustomTag newTag = new CustomTag(5L, "new_tag");
        Mockito.when(serviceMock.create(Mockito.any())).thenReturn(newTag);

        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(new CustomTag("new_tag"));

        mockMvc.perform(post("/custom-tags/")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.entityId").value(5))
                .andExpect(jsonPath("$.name").value("new_tag"));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void createCustomTagExceptionNotValidData() throws Exception {
        CustomException ex = new CustomException("Not valid", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.create(Mockito.any())).thenThrow(ex);

        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(new CustomTag("bad_tag!!!"));

        mockMvc.perform(post("/custom-tags/")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void createCustomTagExceptionResourceAlreadyExist() throws Exception {
        CustomException ex = new CustomException("Resource already exist", CustomErrorCode.RESOURCE_ALREADY_EXIST);
        Mockito.when(serviceMock.create(Mockito.any())).thenThrow(ex);

        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(new CustomTag("tag"));

        mockMvc.perform(post("/custom-tags/")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value("Resource already exist"))
                .andExpect(jsonPath("$.errorCode").value(40901));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }
}