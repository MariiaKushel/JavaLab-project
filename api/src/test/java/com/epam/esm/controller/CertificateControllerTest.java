package com.epam.esm.controller;

import com.epam.esm.ResourceServerApplication;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.TagDto;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CertificateController.class)
@ContextConfiguration(classes = {ResourceServerApplication.class, TestConfig.class})
class CertificateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CertificateService serviceMock;
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
    void findCertificate_guestWithoutJwtAndExistentCertificateId_ok() throws Exception {
        CertificateDto dto = new CertificateDto();
        dto.setId(1L);
        dto.setName("name");
        dto.setDescription("description");
        dto.setDuration(60);
        dto.setPrice(new BigDecimal(100));
        dto.setCreateDate(LocalDateTime.parse("2022-02-01T12:00:00"));
        dto.setLastUpdateDate(LocalDateTime.parse("2022-02-15T13:00:00"));
        TagDto tag1 = new TagDto(1L, "tag1");
        TagDto tag2 = new TagDto(2L, "tag2");
        Set<TagDto> tags = Set.of(tag1, tag2);
        dto.setTags(tags);
        Mockito.when(serviceMock.findById(Mockito.anyLong())).thenReturn(dto);

        mockMvc.perform(get("/certificates/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.duration").value(60))
                .andExpect(jsonPath("$.createDate").value("2022-02-01T12:00:00"))
                .andExpect(jsonPath("$.lastUpdateDate").value("2022-02-15T13:00:00"))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andExpect(jsonPath("$.tags[*]._links", hasSize(2)))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.findAll.href", notNullValue()))
                .andExpect(jsonPath("$._links.search.href", notNullValue()))
                .andExpect(jsonPath("$._links.findByTags.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void findCertificate_userJwtAndExistentCertificateId_ok() throws Exception {
        CertificateDto dto = new CertificateDto();
        dto.setId(1L);
        dto.setName("name");
        dto.setDescription("description");
        dto.setDuration(60);
        dto.setPrice(new BigDecimal(100));
        dto.setCreateDate(LocalDateTime.parse("2022-02-01T12:00:00"));
        dto.setLastUpdateDate(LocalDateTime.parse("2022-02-15T13:00:00"));
        TagDto tag1 = new TagDto(1L, "tag1");
        TagDto tag2 = new TagDto(2L, "tag2");
        Set<TagDto> tags = Set.of(tag1, tag2);
        dto.setTags(tags);
        Mockito.when(serviceMock.findById(Mockito.anyLong())).thenReturn(dto);

        mockMvc.perform(get("/certificates/1")
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.duration").value(60))
                .andExpect(jsonPath("$.createDate").value("2022-02-01T12:00:00"))
                .andExpect(jsonPath("$.lastUpdateDate").value("2022-02-15T13:00:00"))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andExpect(jsonPath("$.tags[*]._links", hasSize(2)))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.findAll.href", notNullValue()))
                .andExpect(jsonPath("$._links.search.href", notNullValue()))
                .andExpect(jsonPath("$._links.findByTags.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void findCertificate_adminJwtAndExistentCertificateId_ok() throws Exception {
        CertificateDto dto = new CertificateDto();
        dto.setId(1L);
        dto.setName("name");
        dto.setDescription("description");
        dto.setDuration(60);
        dto.setPrice(new BigDecimal(100));
        dto.setCreateDate(LocalDateTime.parse("2022-02-01T12:00:00"));
        dto.setLastUpdateDate(LocalDateTime.parse("2022-02-15T13:00:00"));
        TagDto tag1 = new TagDto(1L, "tag1");
        TagDto tag2 = new TagDto(2L, "tag2");
        Set<TagDto> tags = Set.of(tag1, tag2);
        dto.setTags(tags);
        Mockito.when(serviceMock.findById(Mockito.anyLong())).thenReturn(dto);

        mockMvc.perform(get("/certificates/1")
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.duration").value(60))
                .andExpect(jsonPath("$.createDate").value("2022-02-01T12:00:00"))
                .andExpect(jsonPath("$.lastUpdateDate").value("2022-02-15T13:00:00"))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andExpect(jsonPath("$.tags[*]._links", hasSize(2)))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.findAll.href", notNullValue()))
                .andExpect(jsonPath("$._links.search.href", notNullValue()))
                .andExpect(jsonPath("$._links.findByTags.href", notNullValue()))
                .andExpect(jsonPath("$._links.update.href", notNullValue()))
                .andExpect(jsonPath("$._links.delete.href", notNullValue()))
                .andExpect(jsonPath("$._links.create.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void findCertificate_notValidCertificateId_badRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.findById(Mockito.anyLong())).thenThrow(ex);

        mockMvc.perform(get("/certificates/-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void findCertificate_nonExistentCertificateId_notFound() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.when(serviceMock.findById(Mockito.anyLong())).thenThrow(ex);

        mockMvc.perform(get("/certificates/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(999L);
    }

    @Test
    void findAllCertificates_guestWithoutJwtAndCorrectPaginationParameters_ok() throws Exception {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        CertificateDto dto2 = new CertificateDto();
        dto2.setId(2L);
        List<CertificateDto> dtos = List.of(dto1, dto2);
        Mockito.when(serviceMock.findAll(Mockito.anyInt(), Mockito.anyInt())).thenReturn(dtos);
        Mockito.when(serviceMock.findAllLastPage(Mockito.anyInt())).thenReturn(100);

        mockMvc.perform(get("/certificates?page=2&size=3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.certificates", hasSize(2)))
                .andExpect(jsonPath("$._links.firstPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.currentPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.lastPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.search.href", notNullValue()))
                .andExpect(jsonPath("$._links.findByTags.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(serviceMock, Mockito.times(1)).findAllLastPage(Mockito.anyInt());
    }

    @Test
    void findAllCertificates_userJwtAndCorrectPaginationParameters_ok() throws Exception {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        CertificateDto dto2 = new CertificateDto();
        dto2.setId(2L);
        List<CertificateDto> dtos = List.of(dto1, dto2);
        Mockito.when(serviceMock.findAll(Mockito.anyInt(), Mockito.anyInt())).thenReturn(dtos);
        Mockito.when(serviceMock.findAllLastPage(Mockito.anyInt())).thenReturn(100);

        mockMvc.perform(get("/certificates?page=2&size=3")
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.certificates", hasSize(2)))
                .andExpect(jsonPath("$._links.firstPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.currentPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.lastPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.search.href", notNullValue()))
                .andExpect(jsonPath("$._links.findByTags.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(serviceMock, Mockito.times(1)).findAllLastPage(Mockito.anyInt());
    }

    @Test
    void findAllCertificates_adminJwtAndCorrectPaginationParameters_ok() throws Exception {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        CertificateDto dto2 = new CertificateDto();
        dto2.setId(2L);
        List<CertificateDto> dtos = List.of(dto1, dto2);
        Mockito.when(serviceMock.findAll(Mockito.anyInt(), Mockito.anyInt())).thenReturn(dtos);
        Mockito.when(serviceMock.findAllLastPage(Mockito.anyInt())).thenReturn(100);

        mockMvc.perform(get("/certificates?page=2&size=3")
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.certificates", hasSize(2)))
                .andExpect(jsonPath("$._links.firstPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.currentPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.lastPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.search.href", notNullValue()))
                .andExpect(jsonPath("$._links.findByTags.href", notNullValue()))
                .andExpect(jsonPath("$._links.create.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(serviceMock, Mockito.times(1)).findAllLastPage(Mockito.anyInt());
    }

    @Test
    void findCertificate_notValidPaginationParameters_badRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.findAll(Mockito.anyInt(), Mockito.anyInt())).thenThrow(ex);

        mockMvc.perform(get("/certificates?page=-2&size=-3"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void deleteCertificate_guestWithoutJwt_unauthorized() throws Exception {
        mockMvc.perform(delete("/certificates/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteCertificate_userJwt_forbidden() throws Exception {
        mockMvc.perform(delete("/certificates/1")
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteCertificate_adminJwtAndExistentCertificateId_noContent() throws Exception {
        Mockito.doNothing().when(serviceMock).delete(Mockito.anyLong());

        mockMvc.perform(delete("/certificates/1")
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isNoContent());

        Mockito.verify(serviceMock, Mockito.times(1)).delete(Mockito.anyLong());
    }

    @Test
    void deleteCertificate_adminJwtAndNotValidCertificateId_bagRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.doThrow(ex).when(serviceMock).delete(Mockito.anyLong());

        mockMvc.perform(delete("/certificates/-1")
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).delete(Mockito.anyLong());
    }

    @Test
    void deleteCertificate_adminJwtAndNonExistentCertificateId_notFound() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.doThrow(ex).when(serviceMock).delete(Mockito.anyLong());

        mockMvc.perform(delete("/certificates/999")
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(serviceMock, Mockito.times(1)).delete(Mockito.anyLong());
    }

    @Test
    void createCertificate_guestWithoutJwt_unauthorized() throws Exception {
        String jsonContent = mapper.writeValueAsString(new CertificateDto());

        mockMvc.perform(post("/certificates")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createCertificate_userJwtAndCorrectCertificateData_forbidden() throws Exception {
        String jsonContent = mapper.writeValueAsString(new CertificateDto());

        mockMvc.perform(post("/certificates")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void createCertificate_adminJwtAndCorrectCertificateData_ok() throws Exception {
        CertificateDto dto = new CertificateDto();
        dto.setId(55L);
        dto.setTags(new HashSet<>());
        Mockito.when(serviceMock.create(Mockito.any())).thenReturn(dto);

        CertificateDto dtoToCreate = new CertificateDto();
        String jsonContent = mapper.writeValueAsString(dtoToCreate);

        mockMvc.perform(post("/certificates")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(55L))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.findAll.href", notNullValue()))
                .andExpect(jsonPath("$._links.search.href", notNullValue()))
                .andExpect(jsonPath("$._links.findByTags.href", notNullValue()))
                .andExpect(jsonPath("$._links.update.href", notNullValue()))
                .andExpect(jsonPath("$._links.delete.href", notNullValue()))
                .andExpect(jsonPath("$._links.create.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void createCertificate_adminJwtAndNotValidData_bagRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.create(Mockito.any())).thenThrow(ex);

        String jsonContent = mapper.writeValueAsString(new CertificateDto());

        mockMvc.perform(post("/certificates")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void createCertificate_adminJwtAnExistentCertificateData_conflict() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_ALREADY_EXIST);
        Mockito.when(serviceMock.create(Mockito.any())).thenThrow(ex);

        String jsonContent = mapper.writeValueAsString(new TagDto("tag"));

        mockMvc.perform(post("/certificates")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value("Resource already exist: error"))
                .andExpect(jsonPath("$.errorCode").value(40901));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void updateGiftCertificate_guestWithoutJwt_unauthorized() throws Exception {
        String jsonContent = mapper.writeValueAsString(new CertificateDto());

        mockMvc.perform(patch("/certificates/55")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateGiftCertificate_userJwt_forbidden() throws Exception {
        String jsonContent = mapper.writeValueAsString(new CertificateDto());

        mockMvc.perform(patch("/certificates/55")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void updateGiftCertificate_adminJwtAndCorrectCertificateData_ok() throws Exception {
        CertificateDto dto = new CertificateDto();
        dto.setId(55L);
        dto.setTags(new HashSet<>());
        Mockito.when(serviceMock.update(Mockito.anyLong(), Mockito.any())).thenReturn(dto);

        CertificateDto dtoToCreate = new CertificateDto();
        String jsonContent = mapper.writeValueAsString(dtoToCreate);

        mockMvc.perform(patch("/certificates/55")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(55L))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.findAll.href", notNullValue()))
                .andExpect(jsonPath("$._links.search.href", notNullValue()))
                .andExpect(jsonPath("$._links.findByTags.href", notNullValue()))
                .andExpect(jsonPath("$._links.update.href", notNullValue()))
                .andExpect(jsonPath("$._links.delete.href", notNullValue()))
                .andExpect(jsonPath("$._links.create.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).update(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void updateCertificate_adminJwtAndNotValidCertificateData_bagRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.update(Mockito.anyLong(), Mockito.any())).thenThrow(ex);

        String jsonContent = mapper.writeValueAsString(new CertificateDto());

        mockMvc.perform(patch("/certificates/1")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).update(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void updateCertificate_adminJwtAndNonExistentCertificateId_notFound() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.doThrow(ex).when(serviceMock).update(Mockito.anyLong(), Mockito.any());

        String jsonContent = mapper.writeValueAsString(new CertificateDto());

        mockMvc.perform(patch("/certificates/999")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(serviceMock, Mockito.times(1)).update(Mockito.anyLong(), Mockito.any());
    }


    @Test
    void findAllCertificatesByParameters_guestWithoutJwtAdnCorrectSearchParameters_ok() throws Exception {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        CertificateDto dto2 = new CertificateDto();
        dto2.setId(2L);
        List<CertificateDto> dtos = List.of(dto1, dto2);
        Mockito.when(serviceMock.findAllByParameters(Mockito.anyMap(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(dtos);
        Mockito.when(serviceMock.findAllByParametersLastPage(Mockito.anyMap(), Mockito.anyInt())).thenReturn(100);

        mockMvc.perform(get("/certificates/search?page=2&size=3&tag=tag_1&sorting=name.asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.certificates", hasSize(2)))
                .andExpect(jsonPath("$._links.firstPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.currentPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.lastPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.findAll.href", notNullValue()))
                .andExpect(jsonPath("$._links.findByTags.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1))
                .findAllByParameters(Mockito.anyMap(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(serviceMock, Mockito.times(1))
                .findAllByParametersLastPage(Mockito.anyMap(), Mockito.anyInt());
    }

    @Test
    void findAllCertificatesByParameters_userJwtAdnCorrectSearchParameters_ok() throws Exception {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        CertificateDto dto2 = new CertificateDto();
        dto2.setId(2L);
        List<CertificateDto> dtos = List.of(dto1, dto2);
        Mockito.when(serviceMock.findAllByParameters(Mockito.anyMap(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(dtos);
        Mockito.when(serviceMock.findAllByParametersLastPage(Mockito.anyMap(), Mockito.anyInt())).thenReturn(100);

        mockMvc.perform(get("/certificates/search?page=2&size=3&tag=tag_1&sorting=name.asc")
                        .with(jwt().jwt(userJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.certificates", hasSize(2)))
                .andExpect(jsonPath("$._links.firstPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.currentPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.lastPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.findAll.href", notNullValue()))
                .andExpect(jsonPath("$._links.findByTags.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1))
                .findAllByParameters(Mockito.anyMap(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(serviceMock, Mockito.times(1))
                .findAllByParametersLastPage(Mockito.anyMap(), Mockito.anyInt());
    }

    @Test
    void findAllCertificatesByParameters_adminJwtAdnCorrectSearchParameters_ok() throws Exception {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        CertificateDto dto2 = new CertificateDto();
        dto2.setId(2L);
        List<CertificateDto> dtos = List.of(dto1, dto2);
        Mockito.when(serviceMock.findAllByParameters(Mockito.anyMap(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(dtos);
        Mockito.when(serviceMock.findAllByParametersLastPage(Mockito.anyMap(), Mockito.anyInt())).thenReturn(100);

        mockMvc.perform(get("/certificates/search?page=2&size=3&tag=tag_1&sorting=name.asc")
                        .with(jwt().jwt(adminJwt).authorities(customConverter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.certificates", hasSize(2)))
                .andExpect(jsonPath("$._links.firstPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.currentPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.lastPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.findAll.href", notNullValue()))
                .andExpect(jsonPath("$._links.findByTags.href", notNullValue()))
                .andExpect(jsonPath("$._links.create.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1))
                .findAllByParameters(Mockito.anyMap(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(serviceMock, Mockito.times(1))
                .findAllByParametersLastPage(Mockito.anyMap(), Mockito.anyInt());
    }

    @Test
    void findAllCertificatesByParameters_notValidParameters_bagRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.findAllByParameters(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenThrow(ex);

        mockMvc.perform(get("/certificates/search?tag=wront_tag&sorting=name.asc"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1))
                .findAllByParameters(Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
    }
}