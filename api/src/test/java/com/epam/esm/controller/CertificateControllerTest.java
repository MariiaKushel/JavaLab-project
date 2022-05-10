package com.epam.esm.controller;

import com.epam.esm.config.ApiConfig;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.CertificateDto;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CertificateController.class)
@ContextConfiguration(classes = {ApiConfig.class})
class CertificateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CertificateService serviceMock;

    @Test
    void findCertificate_returnCertificate_ok() throws Exception {
        CertificateDto dto = new CertificateDto();
        dto.setId(1L);
        dto.setName("name");
        dto.setDescription("description");
        dto.setDuration(60);
        dto.setPrice(new BigDecimal(100));
        dto.setCreateDate(LocalDateTime.parse("2022-02-01T12:00:00"));
        dto.setLastUpdateDate(LocalDateTime.parse("2022-02-15T13:00:00"));
        TagDto tag1 = new TagDto(1L, "tag1");
        TagDto tag2 = new TagDto(2l, "tag2");
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
                .andExpect(jsonPath("$._links.self.href", notNullValue()));
        Mockito.verify(serviceMock, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void findCertificate_returnExceptionNotValidException_badRequest() throws Exception {
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
    void findCertificate_returnResourceNotFoundException_notFound() throws Exception {
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
    void findAllCertificates_returnCertificateList_ok() throws Exception {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        CertificateDto dto2 = new CertificateDto();
        dto2.setId(2l);
        List<CertificateDto> dtos = List.of(dto1, dto2);
        Mockito.when(serviceMock.findAll(Mockito.anyInt(), Mockito.anyInt())).thenReturn(dtos);
        Mockito.when(serviceMock.count()).thenReturn(1000L);

        mockMvc.perform(get("/certificates?page=2&size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.certificateDtoList", hasSize(2)))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(serviceMock, Mockito.times(1)).count();
    }

    @Test
    void deleteCertificate_returnEmpty_noContent() throws Exception {
        Mockito.doNothing().when(serviceMock).delete(Mockito.anyLong());

        mockMvc.perform(delete("/certificates/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Mockito.verify(serviceMock, Mockito.times(1)).delete(Mockito.anyLong());
    }

    @Test
    void deleteCertificate_returnNotValidDataException_bagRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.doThrow(ex).when(serviceMock).delete(-1L);

        mockMvc.perform(delete("/certificates/-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).delete(-1L);
    }

    @Test
    void deleteCertificate_returnResourceNotFoundException_notFound() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.doThrow(ex).when(serviceMock).delete(999L);

        mockMvc.perform(delete("/certificates/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(serviceMock, Mockito.times(1)).delete(999L);
    }

    @Test
    void createCertificate_returnCertificate_ok() throws Exception {
        CertificateDto dto = new CertificateDto();
        dto.setId(55L);
        Mockito.when(serviceMock.create(Mockito.any())).thenReturn(dto);

        CertificateDto dtoToCreate = new CertificateDto();
        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(dtoToCreate);

        mockMvc.perform(post("/certificates/")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(55L))
                .andExpect(jsonPath("$._links.self.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void createCertificate_returnNotValidDataException_bagRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.create(Mockito.any())).thenThrow(ex);

        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(new CertificateDto());

        mockMvc.perform(post("/certificates/")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void createCertificate_returnResourceAlreadyExistException_conflict() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_ALREADY_EXIST);
        Mockito.when(serviceMock.create(Mockito.any())).thenThrow(ex);

        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(new TagDto("tag"));

        mockMvc.perform(post("/certificates/")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value("Resource already exist: error"))
                .andExpect(jsonPath("$.errorCode").value(40901));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void updateGiftCertificate_returnCertificate_ok() throws Exception {
        CertificateDto dto = new CertificateDto();
        dto.setId(55L);
        Mockito.when(serviceMock.update(Mockito.anyLong(), Mockito.any())).thenReturn(dto);

        CertificateDto dtoToCreate = new CertificateDto();
        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(dtoToCreate);

        mockMvc.perform(patch("/certificates/55")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(55L))
                .andExpect(jsonPath("$._links.self.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1)).update(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void updateCertificate_returnNotValidDataException_bagRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.update(Mockito.anyLong(), Mockito.any())).thenThrow(ex);

        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(new CertificateDto());

        mockMvc.perform(patch("/certificates/1")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).update(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void updateCertificate_returnResourceNotFoundException_notFound() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.doThrow(ex).when(serviceMock).update(Mockito.anyLong(), Mockito.any());

        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(new CertificateDto());

        mockMvc.perform(patch("/certificates/999")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource not found: error"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(serviceMock, Mockito.times(1)).update(Mockito.anyLong(), Mockito.any());
    }


    @Test
    void findAllCertificatesByParameters_returnCertificate_ok() throws Exception {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        CertificateDto dto2 = new CertificateDto();
        dto2.setId(2L);
        List<CertificateDto> dtos = List.of(dto1, dto2);
        Mockito.when(serviceMock.findAllByParameters(Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(dtos);
        Mockito.when(serviceMock.countByParameters(Mockito.anyMap())).thenReturn(1000L);

        mockMvc.perform(get("/certificates/search?page=1&size=2&tag=sea&sorting=name_asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.certificateDtoList", hasSize(2)))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.previousPage.href", notNullValue()))
                .andExpect(jsonPath("$._links.nextPage.href", notNullValue()));

        Mockito.verify(serviceMock, Mockito.times(1))
                .findAllByParameters(Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(serviceMock, Mockito.times(1)).countByParameters(Mockito.anyMap());
    }

    @Test
    void findAllCertificatesByParameters_returnNotValidDataException_bagRequest() throws Exception {
        CustomException ex = new CustomException("error", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.findAllByParameters(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenThrow(ex);
        Mockito.when(serviceMock.countByParameters(Mockito.anyMap())).thenReturn(1000L);

        mockMvc.perform(get("/certificates/?tag=sea&sorting=name_asc"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid data: error"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1))
                .findAllByParameters(Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(serviceMock, Mockito.times(1)).countByParameters(Mockito.anyMap());
    }
}