package com.epam.esm.dao;

import com.epam.esm.dao.entity.CustomTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class CustomTagDaoTest {

    @Autowired
    private CustomTagDao dao;

    public static Object[][] findByNameDataProvider() {
        return new Object[][]{
                {"tag_1", true},
                {"unknown_tag", false}
        };
    }

    @ParameterizedTest
    @MethodSource("findByNameDataProvider")
    void findByName(String name, boolean expected) {
        Optional<CustomTag> tag = dao.findByName(name);
        boolean actual = tag.isPresent();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findTheMostWidelyTag() {
        CustomTag expected = new CustomTag(236L, "tag_236");
        CustomTag actual = dao.findTheMostWidelyTag();
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }
}