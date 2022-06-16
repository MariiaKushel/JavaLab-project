package com.epam.esm.service.validator.impl;

import com.epam.esm.enumeration.SearchParameterName;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.RegistrationFormDto;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.validator.CustomValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
        Assertions.assertEquals(expected, actual);
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
        Assertions.assertEquals(expected, actual);
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
        Assertions.assertEquals(expected, actual);
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
        Assertions.assertEquals(expected, actual);
    }

    public static Object[][] validateSearchParametersDataProvider() {
        Map<SearchParameterName, String> param1 = new HashMap<>();

        Map<SearchParameterName, String> param2 = new HashMap<>();
        param2.put(SearchParameterName.TAG, "tag");
        param2.put(SearchParameterName.NAME, "gift");
        param2.put(SearchParameterName.DESCRIPTION, "description");
        param2.put(SearchParameterName.SORT_BY, "name.asc");

        Map<SearchParameterName, String> param3 = new HashMap<>();
        param3.put(SearchParameterName.SORT_BY, "azaza");

        Map<SearchParameterName, String> param4 = new HashMap<>();
        param4.put(SearchParameterName.TAG, "tag");
        param4.put(SearchParameterName.NAME, "gift");

        Map<SearchParameterName, String> param5 = new HashMap<>();
        return new Object[][]{
                {param1, false},
                {param2, true},
                {param3, false},
                {param4, true},
                {param5, false}
        };
    }

    @ParameterizedTest
    @MethodSource("validateSearchParametersDataProvider")
    void validateSearchParameters(Map<SearchParameterName, String> param, boolean expected) {
        boolean actual = validator.validateSearchParameters(param);
        Assertions.assertEquals(expected, actual);
    }


    public static Object[][] validateUsernameDataProvider() {
        return new Object[][]{
                {"1@gmail.com", true},
                {"1@gmail.com111", false}
        };
    }

    @ParameterizedTest
    @MethodSource("validateUsernameDataProvider")
    void validateUsername(String username, boolean expected) {
        boolean actual = validator.validateUsername(username);
        Assertions.assertEquals(expected, actual);
    }

    public static Object[][] validateRegistrationFormDataProvider() {
        RegistrationFormDto form1 = new RegistrationFormDto("1@gmail.com", "Petr", "123");
        RegistrationFormDto form2 = new RegistrationFormDto("1@gmail.com", "Petr", "123456789");
        RegistrationFormDto form3 = new RegistrationFormDto("1@gmail.com", "<Petr>", "123");
        return new Object[][]{
                {form1, true},
                {form2, false},
                {form3, false}
        };
    }

    @ParameterizedTest
    @MethodSource("validateRegistrationFormDataProvider")
    void validateRegistrationForm(RegistrationFormDto registrationForm, boolean expected) {
        boolean actual = validator.validateRegistrationForm(registrationForm);
        Assertions.assertEquals(expected, actual);
    }

    public static Object[][] validatePageSizeDataProvider() {
        return new Object[][]{
                {1, 5, true},
                {-1, 5, false},
                {1, -5, false},
                {0, 5, false},
                {1, 0, false}
        };
    }

    @ParameterizedTest
    @MethodSource("validatePageSizeDataProvider")
    void validatePageSize(int page, int size, boolean expected) {
        boolean actual = validator.validatePageSize(page, size);
        Assertions.assertEquals(expected, actual);
    }

    public static Object[][] validateCertificateListDataProvider() {
        List<CertificateDto> certificates1 = new ArrayList<>();
        CertificateDto dto2_1 = new CertificateDto(-1L, null, null, new BigDecimal("100"), null, null, null, null);
        List<CertificateDto> certificates2 = List.of(dto2_1);
        CertificateDto dto3_1 = new CertificateDto(1L, null, null, new BigDecimal("-100"), null, null, null, null);
        List<CertificateDto> certificates3 = List.of(dto3_1);
        CertificateDto dto4_1 = new CertificateDto(1L, null, null, new BigDecimal("100"), null, null, null, null);
        List<CertificateDto> certificates4 = List.of(dto4_1);

        return new Object[][]{
                {certificates1, false},
                {certificates2, false},
                {certificates3, false},
                {certificates4, true}
        };
    }

    @ParameterizedTest
    @MethodSource("validateCertificateListDataProvider")
    void validateCertificateList(List<CertificateDto> certificates, boolean expected) {
        boolean actual = validator.validateCertificateList(certificates);
        Assertions.assertEquals(expected, actual);
    }
}