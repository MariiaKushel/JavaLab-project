package com.epam.esm.dao.impl;

import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.dao.entity.Order;
import com.epam.esm.dao.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class OrderDaoImplTest {

    @Autowired
    private OrderDaoImpl dao;
    @Autowired
    private UserDaoImpl userDao;
    @Autowired
    private GiftCertificateDaoImpl giftCertificateDao;

    @Test
    void save() {
        User user = userDao.findById(1L).get();
        GiftCertificate giftCertificate = giftCertificateDao.findById(2L).get();
        Order order = new Order();
        order.setUser(user);
        order.setAmount(new BigDecimal("999.00"));
        order.setGiftCertificatesList(List.of(giftCertificate));

        Order newOrder = dao.save(order);
        Long actualId = newOrder.getId();
        LocalDateTime actualPurchaseDate = newOrder.getPurchaseDate();
        Assertions.assertNotNull(actualId);
        Assertions.assertNotNull(actualPurchaseDate);
    }

    public static Object[][] findByIdDataProvider() {
        return new Object[][]{
                {1L, true},
                {99999999L, false}
        };
    }

    @ParameterizedTest
    @MethodSource("findByIdDataProvider")
    void findById(Long id, boolean expected) {
        Optional<Order> order = dao.findById(id);
        boolean actual = order.isPresent();
        Assertions.assertEquals(actual, expected);
    }

    public static Object[][] findByUserDataProvider() {
        return new Object[][]{
                {1L, 8},
                {99999999L, 0}
        };
    }

    @ParameterizedTest
    @MethodSource("findByUserDataProvider")
    void findByUser(Long userId, int expectedSize) {
        List<Order> orders = dao.findByUser(userId,1,10);
        int actualSize = orders.size();
        Assertions.assertEquals(actualSize, expectedSize);
    }

    public static Object[][] findByIdAndByUserDataProvider() {
        return new Object[][]{
                {1959L, 1L, true},
                {1L, 1L, false}
        };
    }

    @ParameterizedTest
    @MethodSource("findByIdAndByUserDataProvider")
    void findByIdAndByUser(Long orderId , Long userId, boolean expected) {
        Optional<Order> order = dao.findByIdAndByUser(orderId, userId);
        boolean actual = order.isPresent();
        Assertions.assertEquals(actual, expected);
    }
}