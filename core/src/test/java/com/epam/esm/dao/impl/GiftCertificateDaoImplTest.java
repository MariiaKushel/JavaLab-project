package com.epam.esm.dao.impl;

import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.service.SearchParameterName;
import com.epam.esm.service.SortingType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
class GiftCertificateDaoImplTest {

    @Autowired
    private GiftCertificateDaoImpl dao;

    @Test
    void save() {
        GiftCertificate giftCertificate = new GiftCertificate();
        giftCertificate.setName("new_name");
        giftCertificate.setDescription("new description");
        giftCertificate.setPrice(BigDecimal.ZERO);
        giftCertificate.setDuration(0);

        CustomTag newTag = new CustomTag("new_new");
        CustomTag existedTag = new CustomTag(1L, "tag_1");
        Set<CustomTag> tags = new HashSet<>();
        tags.add(newTag);
        tags.add(existedTag);
        giftCertificate.setTags(tags);

        GiftCertificate newGiftCertificate = dao.save(giftCertificate);

        Long actualId = newGiftCertificate.getId();
        LocalDateTime actualCreateDate = newGiftCertificate.getCreateDate();
        LocalDateTime actualLastUpdateDate = newGiftCertificate.getLastUpdateDate();
        int tagsSize = newGiftCertificate.getTags().size();

        Assertions.assertNotNull(actualId);
        Assertions.assertNotNull(actualCreateDate);
        Assertions.assertNotNull(actualLastUpdateDate);
        Assertions.assertEquals(tagsSize, 2);
    }

    @Test
    void update() {
        Long expectedId = 1L;
        String expectedName = "updated_name";
        String expectedDescription = "updated_description";
        BigDecimal expectedPrice = new BigDecimal(999);
        int expectedDuration = 3;

        GiftCertificate oldGiftCertificate = new GiftCertificate();
        oldGiftCertificate.setId(expectedId);
        oldGiftCertificate.setName(expectedName);
        oldGiftCertificate.setDescription(expectedDescription);
        oldGiftCertificate.setPrice(expectedPrice);
        oldGiftCertificate.setDuration(expectedDuration);
        oldGiftCertificate.setCreateDate(LocalDateTime.now());

        CustomTag newTag = new CustomTag("new_tag");
        CustomTag existedTag = new CustomTag(1L, "tag_1");
        Set<CustomTag> tags = new HashSet<>();
        tags.add(newTag);
        tags.add(existedTag);
        oldGiftCertificate.setTags(tags);

        GiftCertificate updatedGiftCertificate = dao.update(oldGiftCertificate);
        Long actualId = updatedGiftCertificate.getId();
        String actualName = updatedGiftCertificate.getName();
        String actualDescription = updatedGiftCertificate.getDescription();
        BigDecimal actualPrice = updatedGiftCertificate.getPrice();
        int actualDuration = updatedGiftCertificate.getDuration();
        int actualSize = updatedGiftCertificate.getTags().size();

        Assertions.assertEquals(actualId, expectedId);
        Assertions.assertEquals(actualName, expectedName);
        Assertions.assertEquals(actualDescription, expectedDescription);
        Assertions.assertEquals(actualPrice, expectedPrice);
        Assertions.assertEquals(actualDuration, expectedDuration);
        Assertions.assertEquals(actualSize, 2);
    }

    @Test
    void delete() {
        Long id = 10000L;
        GiftCertificate giftCertificate = dao.findById(id).get();
        dao.delete(giftCertificate);
        Optional<GiftCertificate> actual = dao.findById(id);
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
        List<GiftCertificate> giftCertificateList = dao.findAll(page, size);
        int actualSize = giftCertificateList.size();
        int expectedSize = size;
        Long actualFirstId = giftCertificateList.get(0).getId();
        Long expectedFirstId = (page - 1) * size + 1L;
        Assertions.assertEquals(actualSize, expectedSize);
        Assertions.assertEquals(actualFirstId, expectedFirstId);
    }

    public static Object[][] findByIdDataProvider() {
        return new Object[][]{
                {1L, true},
                {999999L, false},
                {1397L, true}
        };
    }

    @ParameterizedTest
    @MethodSource("findByIdDataProvider")
    void findById(Long id, boolean expected) {
        Optional<GiftCertificate> giftCertificate = dao.findById(id);
        boolean actual = giftCertificate.isPresent();
        Assertions.assertEquals(actual, expected);
    }

    public static Object[][] findAllByParametersDataProvider() {
        Map<String, String> param1 = new HashMap<>();
        param1.put(SearchParameterName.TAG, "tag_999");

        Map<String, String> param2 = new HashMap<>();
        param2.put(SearchParameterName.NAME, "9999");

        Map<String, String> param3 = new HashMap<>();
        param3.put(SearchParameterName.DESCRIPTION, "1111");

        Map<String, String> param4 = new HashMap<>();
        param4.put(SearchParameterName.SORT_BY, SortingType.DATE_DESC.getType());

        Map<String, String> param5 = new HashMap<>();
        param5.put(SearchParameterName.TAG, "tag_99");
        param5.put(SearchParameterName.NAME, "98");
        param5.put(SearchParameterName.DESCRIPTION, "98");
        param5.put(SearchParameterName.SORT_BY, SortingType.NAME_DESC.getType());
        return new Object[][]{
                {param1, 1, 10, 10},
                {param2, 1, 10, 1},
                {param3, 1, 10, 1},
                {param4, 1, 10, 10},
                {param5, 1, 10, 10},
        };
    }

    @ParameterizedTest
    @MethodSource("findAllByParametersDataProvider")
    void findAllByParameters(Map<String, String> param, int page, int size, int expectedListSize) {
        List<GiftCertificate> giftCertificateList = dao.findAllByParameters(param, page, size);
        int actualListSize = giftCertificateList.size();
        Assertions.assertEquals(actualListSize, expectedListSize);
    }

    @Test
    void countByParameters() {
        Map<String, String> param = new HashMap<>();
        param.put(SearchParameterName.NAME, "999");
        long actual = dao.countByParameters(param);
        long expected = 19L;
        Assertions.assertEquals(actual, expected);
    }

    public static Object[][] findByTagsDataProvider() {
        String[] tags1 = new String[]{};
        String[] tags2 = new String[]{"tag"};
        String[] tags3 = new String[]{"tag_999", "tag_1000"};
        String[] tags4 = new String[]{"tag_1", "tag_2", "tag_3"};
        String[] tags5 = new String[]{"tag_1", "tag_2", "tag_3", "tag_4", "tag_5"};

        return new Object[][]{
                {1, 10, tags1, 0},
                {1, 10, tags2, 0},
                {1, 20, tags3, 20},
                {1, 20, tags4, 10},
                {1, 10, tags5, 0},
        };
    }

    @ParameterizedTest
    @MethodSource("findByTagsDataProvider")
    void findByTags(int page, int size, String[] tags, int expectedListSize) {
        List<GiftCertificate> giftCertificateList = dao.findByTags(page, size, tags);
        int actualListSize = giftCertificateList.size();
        Assertions.assertEquals(actualListSize, expectedListSize);
    }

    public static Object[][] findNameAndDescriptionAndPriceAndDurationDataProvider() {
        String name = "certificate 500";
        String description = "description 500";
        BigDecimal price = new BigDecimal(100);
        int duration1 = 30;
        int duration2 = 61;
        return new Object[][]{
                {name, description, price, duration1, true},
                {name, description, price, duration2, false}
        };
    }

    @ParameterizedTest
    @MethodSource("findNameAndDescriptionAndPriceAndDurationDataProvider")
    void findNameAndDescriptionAndPriceAndDuration(String name, String description,
                                                   BigDecimal price, int duration,
                                                   boolean expected) {
        Optional<GiftCertificate> giftCertificate = dao.findNameAndDescriptionAndPriceAndDuration(name, description,
                price, duration);
        boolean actual = giftCertificate.isPresent();
        Assertions.assertEquals(actual, expected);
    }

}