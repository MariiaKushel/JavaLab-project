package com.epam.esm.service.validator.impl;

import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.service.SearchParameterName;
import com.epam.esm.service.dto.GiftCertificateDto;
import com.epam.esm.service.validator.CustomValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CustomValidatorImplTest {

    private CustomValidator validator;

    public CustomValidatorImplTest() {
        validator = new CustomValidatorImpl();
    }

    public static Object[][] validateEntityIdDataProvider() {
        return new Object[][]{
                {1L, true},
                {-1L, false}
        };
    }

    @ParameterizedTest
    @MethodSource("validateEntityIdDataProvider")
    void validateEntityId(long id, boolean expected) {
        boolean actual = validator.validateEntityId(id);
        Assertions.assertEquals(actual, expected);
    }

    public static Object[][] validateCustomTagDataProvider() {
        return new Object[][]{
                {new CustomTag("tag"), true},
                {new CustomTag("tag tag"), false},
                {new CustomTag("tag!!!"), false}
        };
    }

    @ParameterizedTest
    @MethodSource("validateCustomTagDataProvider")
    void validateCustomTag(CustomTag tag, boolean expected) {
        boolean actual = validator.validateCustomTag(tag);
        Assertions.assertEquals(actual, expected);
    }

    public static Object[][] validateGiftCertificateDtoCreateDataProvider() {
        GiftCertificateDto dto1 = new GiftCertificateDto();
        dto1.setName("gift");
        dto1.setDescription("description");
        dto1.setPrice(new BigDecimal(100));
        dto1.setDuration(60);
        List<CustomTag> tags = new ArrayList<>();
        tags.add(new CustomTag("tag1"));
        tags.add(new CustomTag("tag2"));
        dto1.setTags(tags);

        GiftCertificateDto dto2 = new GiftCertificateDto();
        dto2.setName("gift");
        return new Object[][]{
                {dto1, true},
                {dto2, false}
        };
    }

    @ParameterizedTest
    @MethodSource("validateGiftCertificateDtoCreateDataProvider")
    void validateGiftCertificateDtoCreate(GiftCertificateDto dto, boolean expected) {
        boolean actual = validator.validateGiftCertificateDtoCreate(dto);
        Assertions.assertEquals(actual, expected);
    }

    public static Object[][] validateGiftCertificateDtoUpdateDataProvider() {
        GiftCertificateDto dto1 = new GiftCertificateDto();
        dto1.setPrice(new BigDecimal(100));
        List<CustomTag> tags = new ArrayList<>();
        tags.add(new CustomTag("tag1"));
        tags.add(new CustomTag("tag2"));
        dto1.setTags(tags);

        GiftCertificateDto dto2 = new GiftCertificateDto();
        dto2.setPrice(new BigDecimal(-1));
        return new Object[][]{
                {dto1, true},
                {dto2, false}
        };
    }

    @ParameterizedTest
    @MethodSource("validateGiftCertificateDtoUpdateDataProvider")
    void validateGiftCertificateDtoUpdate(GiftCertificateDto dto, boolean expected) {
        boolean actual = validator.validateGiftCertificateDtoUpdate(dto);
        Assertions.assertEquals(actual, expected);
    }

    public static Object[][] validateSearchParametersDataProvider() {
        Map<String, String> param1 = new HashMap<>();

        Map<String, String> param2 = new HashMap<>();
        param2.put(SearchParameterName.TAG, "tag");
        param2.put(SearchParameterName.NAME, "gift");
        param2.put(SearchParameterName.DESCRIPTION, "description");
        param2.put(SearchParameterName.SORTING, "NAME_ASC");

        Map<String, String> param3 = new HashMap<>();
        param3.put(SearchParameterName.SORTING, "azaza");
        return new Object[][]{
                {param1, true},
                {param2, true},
                {param3, false}
        };
    }

    @ParameterizedTest
    @MethodSource("validateSearchParametersDataProvider")
    void validateSearchParameters(Map<String, String> param, boolean expected) {
        boolean actual = validator.validateSearchParameters(param);
        Assertions.assertEquals(actual, expected);
    }
}