package com.epam.esm.service.validator.impl;

import com.epam.esm.service.SearchParameterName;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.validator.CustomValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class CustomValidatorImplTest {

    private CustomValidator validator = new CustomValidatorImpl();

    public static Object[][] validateEntityIdDataProvider() {
        return new Object[][]{
                {1L, true},
                {-1L, false},
        };
    }

    @ParameterizedTest
    @MethodSource("validateEntityIdDataProvider")
    void validateEntityId(long id, boolean expected) {
        boolean actual = validator.validateEntityId(id);
        Assertions.assertEquals(actual, expected);
    }

    public static Object[][] validateTagDtoDataProvider() {
        return new Object[][]{
                {new TagDto("tag"), true},
                {new TagDto("tag tag"), false},
                {new TagDto("tag!!!"), false}
        };
    }

    @ParameterizedTest
    @MethodSource("validateTagDtoDataProvider")
    void validateTagDto(TagDto tag, boolean expected) {
        boolean actual = validator.validateTagDto(tag);
        Assertions.assertEquals(actual, expected);
    }

    public static Object[][] validateCertificateDtoCreateDataProvider() {
        CertificateDto dto1 = new CertificateDto();
        dto1.setName("gift");
        dto1.setDescription("description");
        dto1.setPrice(new BigDecimal(100));
        dto1.setDuration(60);
        Set<TagDto> tags = new HashSet<>();
        tags.add(new TagDto("tag1"));
        tags.add(new TagDto("tag2"));
        dto1.setTags(tags);

        CertificateDto dto2 = new CertificateDto();
        dto2.setName("gift");

        CertificateDto dto3 = new CertificateDto();

        CertificateDto dto4 = new CertificateDto();
        dto4.setName("gift");
        dto4.setDescription("description");
        dto4.setPrice(new BigDecimal(100));
        dto4.setDuration(60);

        return new Object[][]{
                {dto1, true},
                {dto2, false},
                {dto3, false},
                {dto4, false}
        };
    }

    @ParameterizedTest
    @MethodSource("validateCertificateDtoCreateDataProvider")
    void validateCertificateDtoCreate(CertificateDto dto, boolean expected) {
        boolean actual = validator.validateCertificateDtoCreate(dto);
        Assertions.assertEquals(actual, expected);
    }

    public static Object[][] validateCertificateDtoUpdateDataProvider() {
        CertificateDto dto1 = new CertificateDto();
        dto1.setPrice(new BigDecimal(100));
        Set<TagDto> tags = new HashSet<>();
        tags.add(new TagDto("tag1"));
        tags.add(new TagDto("tag2"));
        dto1.setTags(tags);

        CertificateDto dto2 = new CertificateDto();
        dto2.setPrice(new BigDecimal(-1));

        CertificateDto dto3 = new CertificateDto();

        CertificateDto dto4 = new CertificateDto();
        dto4.setDescription("new description");

        CertificateDto dto5 = new CertificateDto();
        dto5.setCreateDate(LocalDateTime.now());

        return new Object[][]{
                {dto1, true},
                {dto2, false},
                {dto3, false},
                {dto4, true},
                {dto5, false}
        };
    }

    @ParameterizedTest
    @MethodSource("validateCertificateDtoUpdateDataProvider")
    void validateCertificateDtoUpdate(CertificateDto dto, boolean expected) {
        boolean actual = validator.validateCertificateDtoUpdate(dto);
        Assertions.assertEquals(actual, expected);
    }

    public static Object[][] validateSearchParametersDataProvider() {
        Map<String, String> param1 = new HashMap<>();

        Map<String, String> param2 = new HashMap<>();
        param2.put(SearchParameterName.TAG, "tag");
        param2.put(SearchParameterName.NAME, "gift");
        param2.put(SearchParameterName.DESCRIPTION, "description");
        param2.put(SearchParameterName.SORT_BY, "NAME_ASC");

        Map<String, String> param3 = new HashMap<>();
        param3.put(SearchParameterName.SORT_BY, "azaza");

        Map<String, String> param4 = new HashMap<>();
        param4.put(SearchParameterName.TAG, "tag");
        param4.put(SearchParameterName.NAME, "gift");

        Map<String, String> param5 = new HashMap<>();
        param5.put("unknown", "azaza");
        param5.put(SearchParameterName.NAME, "gift");

        Map<String, String> param6 = new HashMap<>();
        param6.put("unknown", "azaza");

        Map<String, String> param7 = new HashMap<>();
        return new Object[][]{
                {param1, false},
                {param2, true},
                {param3, false},
                {param4, true},
                {param5, false},
                {param6, false},
                {param7, false}
        };
    }

    @ParameterizedTest
    @MethodSource("validateSearchParametersDataProvider")
    void validateSearchParameters(Map<String, String> param, boolean expected) {
        boolean actual = validator.validateSearchParameters(param);
        Assertions.assertEquals(actual, expected);
    }
}