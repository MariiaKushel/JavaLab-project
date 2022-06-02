package com.epam.esm.controller;

import com.epam.esm.config.ApiConfig;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.properties.JwtProperty;
import com.epam.esm.service.TagService;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.util.impl.AdminCollectionLinkCreator;
import com.epam.esm.util.impl.AdminSingleEntityLinkCreator;
import com.epam.esm.util.impl.CommonCollectionLinkCreator;
import com.epam.esm.util.impl.CommonSingleEntityLinkCreator;
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
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
@ContextConfiguration(classes = {ApiConfig.class, TestConfig.class})
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService serviceMock;
    @MockBean
    private UserService userServiceMock;
    @Autowired
    private AdminSingleEntityLinkCreator adminSingleEntityLinkCreator;
    @Autowired
    private CommonSingleEntityLinkCreator commonSingleEntityLinkCreator;
    @Autowired
    private AdminCollectionLinkCreator adminCollectionLinkCreator;
    @Autowired
    private CommonCollectionLinkCreator commonCollectionLinkCreator;
    @Autowired
    private JwtProperty jwtProperty;
    @Autowired
    private  ObjectMapper mapper;

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
    void findTag_guestWithoutJwtCookieAndExistentTagId_ok() throws Exception {
        TagDto tag = new TagDto(1L, "tag");
        Mockito.when(serviceMock.findById(Mockito.anyLong())).thenReturn(tag);

        mockMvc.perform(get("/tags/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("tag"))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.findAll.href", notNullValue()))
                .andExpect(jsonPath("$._links.findTheMostWidelyTag.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void findTag_userJwtCookieAndExistentTagId_ok() throws Exception {
        TagDto tag = new TagDto(1L, "tag");
        Mockito.when(serviceMock.findById(Mockito.anyLong())).thenReturn(tag);

        mockMvc.perform(get("/tags/1")
                        .cookie(userJwtCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("tag"))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.findAll.href", notNullValue()))
                .andExpect(jsonPath("$._links.findTheMostWidelyTag.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void findTag_adminJwtCookieAndExistentTagId_ok() throws Exception {
        TagDto tag = new TagDto(1L, "tag");
        Mockito.when(serviceMock.findById(Mockito.anyLong())).thenReturn(tag);

        mockMvc.perform(get("/tags/1")
                        .cookie(adminJwtCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("tag"))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.findAll.href", notNullValue()))
                .andExpect(jsonPath("$._links.findTheMostWidelyTag.href", notNullValue()))
                .andExpect(jsonPath("$._links.delete.href", notNullValue()))
                .andExpect(jsonPath("$._links.create.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void findTag_notValidTagId_badRequest() throws Exception {
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
    void findAllTag_guestWithoutJwtCookieAndCorrectPaginationParameters_ok() throws Exception {
        TagDto tag1 = new TagDto(1L, "tag1");
        TagDto tag2 = new TagDto(2L, "tag2");
        TagDto tag3 = new TagDto(3L, "tag3");
        List<TagDto> tags = List.of(tag1, tag2, tag3);

        Mockito.when(serviceMock.findAll(Mockito.anyInt(), Mockito.anyInt())).thenReturn(tags);
        Mockito.when(serviceMock.findAllLastPage(Mockito.anyInt())).thenReturn(100);

        mockMvc.perform(get("/tags?page=2&size=3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.tags", hasSize(3)))
                .andExpect(jsonPath("$._links.firstPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.currentPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.lastPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.findTheMostWidelyTag.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(serviceMock, Mockito.times(1)).findAllLastPage(Mockito.anyInt());
    }

    @Test
    void findAllTag_userJwtCookieAndCorrectPaginationParameters_ok() throws Exception {
        TagDto tag1 = new TagDto(1L, "tag1");
        TagDto tag2 = new TagDto(2L, "tag2");
        TagDto tag3 = new TagDto(3L, "tag3");
        List<TagDto> tags = List.of(tag1, tag2, tag3);

        Mockito.when(serviceMock.findAll(Mockito.anyInt(), Mockito.anyInt())).thenReturn(tags);
        Mockito.when(serviceMock.findAllLastPage(Mockito.anyInt())).thenReturn(100);

        mockMvc.perform(get("/tags?page=2&size=3")
                        .cookie(userJwtCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.tags", hasSize(3)))
                .andExpect(jsonPath("$._links.firstPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.currentPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.lastPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.findTheMostWidelyTag.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(serviceMock, Mockito.times(1)).findAllLastPage(Mockito.anyInt());
    }

    @Test
    void findAllTag_adminJwtCookieAndCorrectPaginationParameters_ok() throws Exception {
        TagDto tag1 = new TagDto(1L, "tag1");
        TagDto tag2 = new TagDto(2L, "tag2");
        TagDto tag3 = new TagDto(3L, "tag3");
        List<TagDto> tags = List.of(tag1, tag2, tag3);

        Mockito.when(serviceMock.findAll(Mockito.anyInt(), Mockito.anyInt())).thenReturn(tags);
        Mockito.when(serviceMock.findAllLastPage(Mockito.anyInt())).thenReturn(100);

        mockMvc.perform(get("/tags?page=2&size=3")
                        .cookie(adminJwtCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.tags", hasSize(3)))
                .andExpect(jsonPath("$._links.firstPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.currentPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.lastPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.findTheMostWidelyTag.href", notNullValue()))
                .andExpect(jsonPath("$._links.create.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(serviceMock, Mockito.times(1)).findAllLastPage(Mockito.anyInt());
    }

    @Test
    void findAllTag_notValidPaginationParameters_badRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.findAll(Mockito.anyInt(), Mockito.anyInt())).thenThrow(ex);

        mockMvc.perform(get("/tags?page=-2&size=-3"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void deleteTag_guestWithoutJwtCookie_unauthorized() throws Exception {
        mockMvc.perform(delete("/tags/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteTag_userJwtCookie_forbidden() throws Exception {
        mockMvc.perform(delete("/tags/1")
                        .cookie(userJwtCookie)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteTag_adminJwtCookieAndExistentTagId_noContent() throws Exception {
        Mockito.doNothing().when(serviceMock).delete(Mockito.anyLong());

        mockMvc.perform(delete("/tags/1")
                        .cookie(adminJwtCookie)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());

        Mockito.verify(serviceMock, Mockito.times(1)).delete(Mockito.anyLong());
    }

    @Test
    void deleteTag_adminJwtCookieAndNotValidTagId_bagRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.doThrow(ex).when(serviceMock).delete(Mockito.anyLong());

        mockMvc.perform(delete("/tags/-1")
                        .cookie(adminJwtCookie)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).delete(Mockito.anyLong());
    }

    @Test
    void deleteTag_adminJwtCookieAndNonExistentTagId_notFound() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.doThrow(ex).when(serviceMock).delete(999L);

        mockMvc.perform(delete("/tags/999")
                        .cookie(adminJwtCookie)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(serviceMock, Mockito.times(1)).delete(999L);
    }

    @Test
    void createTag_guestWithoutJwtCookie_unauthorized() throws Exception {
        mockMvc.perform(post("/tags")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createTag_userJwtCookie_forbidden() throws Exception {
        mockMvc.perform(post("/tags")
                        .cookie(userJwtCookie)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void createTag_adminJwtCookieAndCorrectTadData_ok() throws Exception {
        TagDto newTag = new TagDto(5L, "new_tag");
        Mockito.when(serviceMock.create(Mockito.any())).thenReturn(newTag);

        String jsonContent = mapper.writeValueAsString(new TagDto("new_tag"));

        mockMvc.perform(post("/tags")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .cookie(adminJwtCookie)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("new_tag"))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.findAll.href", notNullValue()))
                .andExpect(jsonPath("$._links.findTheMostWidelyTag.href", notNullValue()))
                .andExpect(jsonPath("$._links.delete.href", notNullValue()))
                .andExpect(jsonPath("$._links.create.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void createTag_adminJwtCookieAndNotValidTadData_bagRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.create(Mockito.any())).thenThrow(ex);

        String jsonContent = mapper.writeValueAsString(new CustomTag("bad_tag!!!"));

        mockMvc.perform(post("/tags")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .cookie(adminJwtCookie)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void createTag_adminJwtCookieAndExistentTagData_conflict() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_ALREADY_EXIST);
        Mockito.when(serviceMock.create(Mockito.any())).thenThrow(ex);

        String jsonContent = mapper.writeValueAsString(new TagDto("tag"));

        mockMvc.perform(post("/tags")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .cookie(adminJwtCookie)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value("Resource already exist: error"))
                .andExpect(jsonPath("$.errorCode").value(40901));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void findTheMostWidelyTag_guestWithoutJwtCookie_ok() throws Exception {
        TagDto tag = new TagDto(1L, "tag");
        Mockito.when(serviceMock.findTheMostWidelyTag()).thenReturn(tag);

        mockMvc.perform(get("/tags/the-most-widely"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("tag"))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.findAll.href", notNullValue()))
                .andExpect(jsonPath("$._links.findTheMostWidelyTag.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findTheMostWidelyTag();
    }

    @Test
    void findTheMostWidelyTag_userJwtCookie_ok() throws Exception {
        TagDto tag = new TagDto(1L, "tag");
        Mockito.when(serviceMock.findTheMostWidelyTag()).thenReturn(tag);

        mockMvc.perform(get("/tags/the-most-widely")
                        .cookie(userJwtCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("tag"))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.findAll.href", notNullValue()))
                .andExpect(jsonPath("$._links.findTheMostWidelyTag.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findTheMostWidelyTag();
    }

    @Test
    void findTheMostWidelyTag_adminJwtCookie_ok() throws Exception {
        TagDto tag = new TagDto(1L, "tag");
        Mockito.when(serviceMock.findTheMostWidelyTag()).thenReturn(tag);

        mockMvc.perform(get("/tags/the-most-widely")
                        .cookie(adminJwtCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("tag"))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.findAll.href", notNullValue()))
                .andExpect(jsonPath("$._links.findTheMostWidelyTag.href", notNullValue()))
                .andExpect(jsonPath("$._links.delete.href", notNullValue()))
                .andExpect(jsonPath("$._links.create.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findTheMostWidelyTag();
    }
}