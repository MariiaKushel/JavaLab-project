package com.epam.esm.dao.impl;

import com.epam.esm.config.DataBaseConfigurationTest;
import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.entity.CustomTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DataBaseConfigurationTest.class)
@ActiveProfiles("test")
class CustomTagDaoImplTest {

    @Autowired
    private CustomTagDao dao;

    @Test
    void save() {
        CustomTag customTag = new CustomTag(0, "");
        CustomTag newCustomTag = dao.save(customTag);
        long actualId = newCustomTag.getId();
        long expectedId = 9;
        Assertions.assertEquals(actualId, expectedId);
    }

    public static Object[][] deleteDataProvider() {
        return new Object[][]{
                {1L, 1},
                {999L, 0}
        };
    }

    @ParameterizedTest
    @MethodSource("deleteDataProvider")
    void delete(Long id, int expected) {
        int sizeBefore = dao.findAll().size();
        dao.delete(id);
        int sizeAfter = dao.findAll().size();
        int actual = sizeBefore - sizeAfter;
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findAll() {
        List<CustomTag> tags = dao.findAll();
        int actualSize = tags.size();
        int expectedSize = 8;
        Assertions.assertEquals(actualSize, expectedSize);
    }

    public static Object[][] findByIdDataProvider() {
        return new Object[][]{
                {1L, true},
                {999L, false}
        };
    }

    @ParameterizedTest
    @MethodSource("findByIdDataProvider")
    void findById(long id, boolean expected) {
        Optional<CustomTag> tag = dao.findById(id);
        boolean actual = tag.isPresent();
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findAllByGiftCertificateId() {
        List<CustomTag> tags = dao.findAllByGiftCertificateId(1L);
        int actualSize = tags.size();
        int expectedSize = 3;
        Assertions.assertEquals(actualSize, expectedSize);
    }

    public static Object[][] findByNameDataProvider() {
        return new Object[][]{
                {"tag_101", true},
                {"azaza", false}
        };
    }

    @ParameterizedTest
    @MethodSource("findByNameDataProvider")
    void findByName(String name, boolean expected) {
        Optional<CustomTag> tag = dao.findByName(name);
        boolean actual = tag.isPresent();
        Assertions.assertEquals(actual, expected);
    }
}