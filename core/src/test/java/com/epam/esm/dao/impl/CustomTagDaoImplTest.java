package com.epam.esm.dao.impl;

import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.entity.CustomTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class CustomTagDaoImplTest {

    @Autowired
    private CustomTagDao dao;

    @Test
    void save() {
        String newName = "new_name";
        CustomTag customTag = new CustomTag(newName);
        CustomTag newCustomTag = dao.save(customTag);
        String actualName = newCustomTag.getName();
        String expectedName = newName;
        Long actualId = newCustomTag.getId();
        Assertions.assertEquals(actualName, expectedName);
        Assertions.assertNotNull(actualId);
    }

    @Test
    void delete() {
        Long id = 1000L;
        CustomTag tag = dao.findById(id).get();
        dao.delete(tag);
        Optional<CustomTag> actual = dao.findById(id);
        Assertions.assertTrue(actual.isEmpty());
    }


    public static Object[][] findAllDataProvider() {
        return new Object[][]{
                {3, 3},
                {2, 4}
        };
    }

    @ParameterizedTest
    @MethodSource("findAllDataProvider")
    void findAll(int page, int size) {
        List<CustomTag> tags = dao.findAll(page, size);
        int actualSize = tags.size();
        int expectedSize = size;
        Long actualFirstId = tags.get(0).getId();
        Long expectedFirstId = (page - 1) * size + 1L;
        Assertions.assertEquals(actualSize, expectedSize);
        Assertions.assertEquals(actualFirstId, expectedFirstId);
    }

    public static Object[][] findByIdDataProvider() {
        return new Object[][]{
                {1L, true},
                {99999999L, false}
        };
    }

    @ParameterizedTest
    @MethodSource("findByIdDataProvider")
    void findById(long id, boolean expected) {
        Optional<CustomTag> tag = dao.findById(id);
        boolean actual = tag.isPresent();
        Assertions.assertEquals(actual, expected);
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


    @Test
    void findTheMostWidelyTags() {
        CustomTag tag = dao.findTheMostWidelyTags();
        Assertions.assertNotNull(tag);
    }
}