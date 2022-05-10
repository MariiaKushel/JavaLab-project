package com.epam.esm.controller;

import com.epam.esm.config.ApiConfig;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.TagDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
@ContextConfiguration(classes = {ApiConfig.class})
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService serviceMock;

    @Test
    void findTag_existentTagId_ok() throws Exception {
        TagDto tag = new TagDto(1L, "tag");
        Mockito.when(serviceMock.findById(1L)).thenReturn(tag);

        mockMvc.perform(get("/tags/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("tag"))
                .andExpect(jsonPath("$._links.self.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(1L);
    }

    @Test
    void findTag_notValidData_badRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.findById(-1L)).thenThrow(ex);

        mockMvc.perform(get("/tags/-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(-1L);
    }

    @Test
    void findTag_nonExistentTagId_notFound() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.when(serviceMock.findById(999L)).thenThrow(ex);

        mockMvc.perform(get("/tags/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(999L);
    }

    @Test
    void findAllTag_correctPaginationParameters_ok() throws Exception {
        TagDto tag1 = new TagDto(1L, "tag1");
        TagDto tag2 = new TagDto(2L, "tag2");
        TagDto tag3 = new TagDto(3L, "tag3");
        List<TagDto> tags = List.of(tag1, tag2, tag3);

        Mockito.when(serviceMock.findAll(2, 3)).thenReturn(tags);
        Mockito.when(serviceMock.count()).thenReturn(1000L);

        mockMvc.perform(get("/tags?page=2&size=3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.tagDtoList", hasSize(3)))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll(2, 3);
        Mockito.verify(serviceMock, Mockito.times(1)).count();
    }

    @Test
    void deleteTag_existentTagId_noContent() throws Exception {
        Mockito.doNothing().when(serviceMock).delete(Mockito.anyLong());

        mockMvc.perform(delete("/tags/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Mockito.verify(serviceMock, Mockito.times(1)).delete(Mockito.anyLong());
    }

    @Test
    void deleteTag_notValidData_bagRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.doThrow(ex).when(serviceMock).delete(-1L);

        mockMvc.perform(delete("/tags/-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).delete(-1L);
    }

    @Test
    void deleteTag_nonExistentTagId_notFound() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.doThrow(ex).when(serviceMock).delete(999L);

        mockMvc.perform(delete("/tags/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(serviceMock, Mockito.times(1)).delete(999L);
    }

    @Test
    void createTag_correctTadData_ok() throws Exception {
        TagDto newTag = new TagDto(5L, "new_tag");
        Mockito.when(serviceMock.create(Mockito.any())).thenReturn(newTag);

        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(new TagDto("new_tag"));

        mockMvc.perform(post("/tags/")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("new_tag"))
                .andExpect(jsonPath("$._links.self.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void createTag_notValidData_bagRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.create(Mockito.any())).thenThrow(ex);

        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(new CustomTag("bad_tag!!!"));

        mockMvc.perform(post("/tags/")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void createTag_existentTagData_conflict() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_ALREADY_EXIST);
        Mockito.when(serviceMock.create(Mockito.any())).thenThrow(ex);

        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(new TagDto("tag"));

        mockMvc.perform(post("/tags/")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value("Resource already exist: error"))
                .andExpect(jsonPath("$.errorCode").value(40901));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void findTheMostWidelyTag_emptyParam_ok() throws Exception {
        TagDto tag = new TagDto(1L, "tag");
        Mockito.when(serviceMock.findTheMostWidelyTag()).thenReturn(tag);

        mockMvc.perform(get("/tags/the-most-widely"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("tag"))
                .andExpect(jsonPath("$._links.self.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findTheMostWidelyTag();
    }
}