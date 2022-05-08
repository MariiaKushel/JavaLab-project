package com.epam.esm.dao.impl;

import com.epam.esm.config.DataBaseConfigurationTest;
import com.epam.esm.dao.ColumnName;
import com.epam.esm.dao.entity.GiftCertificate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DataBaseConfigurationTest.class)
@ActiveProfiles("test")
class GiftCertificateDaoImplTest {

    @Autowired
    private GiftCertificateDaoImpl dao;

    @Test
    void save() {
        GiftCertificate giftCertificate = GiftCertificate.newBuilder()
                .setName("")
                .setDescription("")
                .setPrice(BigDecimal.ZERO)
                .setDuration(0)
                .build();
        GiftCertificate newGiftCertificate = dao.save(giftCertificate);
        long actualId = newGiftCertificate.getId();
        long expectedId = 13;
        Assertions.assertEquals(actualId, expectedId);
    }

    @Test
    void update() {
        Map<String, String> param = new HashMap<>();
        param.put(ColumnName.ID_GIFT_CERTIFICATE, "1");
        param.put(ColumnName.NAME_GIFT_CERTIFICATE, "new_name");
        param.put(ColumnName.DESCRIPTION, "new_description");
        param.put(ColumnName.PRICE, "999.99");
        param.put(ColumnName.DURATION, "1");
        GiftCertificate giftCertificate = dao.update(param);
        Assertions.assertEquals(giftCertificate.getName(), "new_name");
        Assertions.assertEquals(giftCertificate.getDescription(), "new_description");
        Assertions.assertEquals(giftCertificate.getPrice(), new BigDecimal("999.99"));
        Assertions.assertEquals(giftCertificate.getDuration(), 1);
    }

    @Test
    void updateEmpty() {
        LocalDateTime lastUpdateDateBefore = dao.findById(1L).get().getLastUpdateDate();
        Map<String, String> param = new HashMap<>();
        param.put(ColumnName.ID_GIFT_CERTIFICATE, "1");
        LocalDateTime lastUpdateDateAfter = dao.update(param).getLastUpdateDate();
        Assertions.assertFalse(lastUpdateDateBefore.equals(lastUpdateDateAfter));
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
    void testFindAll() {
        List<GiftCertificate> giftCertificateList = dao.findAll();
        int actualSize = giftCertificateList.size();
        int expectedSize = 12;
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
    void testFindById(Long id, boolean expected) {
        Optional<GiftCertificate> giftCertificate = dao.findById(id);
        boolean actual = giftCertificate.isPresent();
        Assertions.assertEquals(actual, expected);
    }

}