package com.epam.esm.controller;

import com.epam.esm.config.WebConfig;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.dto.GiftCertificateDto;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WebConfig.class, TestContext.class})
@WebAppConfiguration
class GiftCertificateControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private GiftCertificateService serviceMock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        Mockito.reset(serviceMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void findGiftCertificate() throws Exception {
        GiftCertificateDto dto = new GiftCertificateDto();
        dto.setDtoId(1L);
        dto.setName("name");
        dto.setDescription("description");
        dto.setDuration(60);
        dto.setPrice(new BigDecimal(100));
        dto.setCreateDate(LocalDateTime.parse("2022-02-01T12:00:00"));
        dto.setLastUpdateDate(LocalDateTime.parse("2022-02-15T13:00:00"));
        CustomTag tag1 = new CustomTag(1L, "tag1");
        CustomTag tag2 = new CustomTag(2l, "tag2");
        List<CustomTag> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);
        dto.setTags(tags);
        Mockito.when(serviceMock.findById(Mockito.anyLong())).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/gift-certificates/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dtoId").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.duration").value(60))
                .andExpect(jsonPath("$.createDate").value("2022-02-01T12:00:00.000"))
                .andExpect(jsonPath("$.lastUpdateDate").value("2022-02-15T13:00:00.000"))
                .andExpect(jsonPath("$.tags", hasSize(2)));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void findGiftCertificateExceptionNotValid() throws Exception {
        CustomException ex = new CustomException("Not valid", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.findById(Mockito.anyLong())).thenThrow(ex);

        mockMvc.perform(get("/gift-certificates/-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void findGiftCertificateExceptionResourceNotFound() throws Exception {
        Mockito.when(serviceMock.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/gift-certificates/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(serviceMock, Mockito.times(1)).findById(999L);
    }

    @Test
    void findAllGiftCertificates() throws Exception {
        GiftCertificateDto dto1 = new GiftCertificateDto();
        dto1.setDtoId(1L);
        GiftCertificateDto dto2 = new GiftCertificateDto();
        dto2.setDtoId(2l);
        List<GiftCertificateDto> dtos = new ArrayList<>();
        dtos.add(dto1);
        dtos.add(dto2);
        Mockito.when(serviceMock.findAll()).thenReturn(dtos);

        mockMvc.perform(get("/gift-certificates"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll();
    }

    @Test
    void deleteGiftCertificate() throws Exception {
        Mockito.doNothing().when(serviceMock).delete(Mockito.anyLong());

        mockMvc.perform(delete("/gift-certificates/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Mockito.verify(serviceMock, Mockito.times(1)).delete(Mockito.anyLong());
    }

    @Test
    void deleteGiftCertificateExceptionNotValidData() throws Exception {
        CustomException ex = new CustomException("Not valid", CustomErrorCode.NOT_VALID_DATA);
        Mockito.doThrow(ex).when(serviceMock).delete(-1L);

        mockMvc.perform(delete("/gift-certificates/-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).delete(-1L);
    }

    @Test
    void createGiftCertificate() throws Exception {
        GiftCertificateDto dto = new GiftCertificateDto();
        dto.setDtoId(55L);
        Mockito.when(serviceMock.create(Mockito.any())).thenReturn(dto);

        GiftCertificateDto dtoToCreate = new GiftCertificateDto();
        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(dtoToCreate);

        mockMvc.perform(post("/gift-certificates/")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dtoId").value(55L));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void createGiftCertificateExceptionNotValidData() throws Exception {
        CustomException ex = new CustomException("Not valid", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.create(Mockito.any())).thenThrow(ex);

        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(new GiftCertificateDto());

        mockMvc.perform(post("/gift-certificates/")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void updateGiftCertificate() throws Exception {
        GiftCertificateDto dto = new GiftCertificateDto();
        dto.setDtoId(55L);
        Mockito.when(serviceMock.update(Mockito.anyLong(), Mockito.any())).thenReturn(dto);

        GiftCertificateDto dtoToCreate = new GiftCertificateDto();
        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(dtoToCreate);

        mockMvc.perform(patch("/gift-certificates/55")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dtoId").value(55L));

        Mockito.verify(serviceMock, Mockito.times(1)).update(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void updateGiftCertificateExceptionNotValidData() throws Exception {
        CustomException ex = new CustomException("Not valid", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.update(Mockito.anyLong(), Mockito.any())).thenThrow(ex);

        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(new GiftCertificateDto());

        mockMvc.perform(patch("/gift-certificates/55")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).update(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void updateGiftCertificateExceptionResourceNotFound() throws Exception {
        CustomException ex = new CustomException("Not found", CustomErrorCode.RESOURCE_NOT_FOUND);
        Mockito.when(serviceMock.update(Mockito.anyLong(), Mockito.any())).thenThrow(ex);

        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(new GiftCertificateDto());

        mockMvc.perform(patch("/gift-certificates/55")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Not found"))
                .andExpect(jsonPath("$.errorCode").value(40401));

        Mockito.verify(serviceMock, Mockito.times(1)).update(Mockito.anyLong(), Mockito.any());
    }


    @Test
    void findAllGiftCertificatesByParameters() throws Exception {
        GiftCertificateDto dto1 = new GiftCertificateDto();
        dto1.setDtoId(1L);
        GiftCertificateDto dto2 = new GiftCertificateDto();
        dto1.setDtoId(2L);
        List<GiftCertificateDto> dtos = new ArrayList<>();
        dtos.add(dto1);
        dtos.add(dto2);
        Mockito.when(serviceMock.findAllByParameters(Mockito.any())).thenReturn(dtos);

        mockMvc.perform(get("/gift-certificates/?tag=sea&sorting=name_asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));

        Mockito.verify(serviceMock, Mockito.times(1)).findAllByParameters(Mockito.any());
    }

    @Test
    void findAllGiftCertificatesByParametersExceptionNotValidData() throws Exception {
        CustomException ex = new CustomException("Not valid", CustomErrorCode.NOT_VALID_DATA);
        Mockito.when(serviceMock.findAllByParameters(Mockito.any())).thenThrow(ex);

        mockMvc.perform(get("/gift-certificates/?tag=sea&sorting=name_asc"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not valid"))
                .andExpect(jsonPath("$.errorCode").value(40001));

        Mockito.verify(serviceMock, Mockito.times(1)).findAllByParameters(Mockito.any());
    }
}