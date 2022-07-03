package com.epam.esm.dao;

import com.epam.esm.dao.entity.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class OrderDaoTest {

    @Autowired
    private OrderDao dao;

    public static Object[][] findAllByUserIdDataProvider() {
        Pageable paging = PageRequest.of(0, 10);
        return new Object[][]{
                {1L, 9, paging},
                {99999999L, 0, paging}
        };
    }

    @ParameterizedTest
    @MethodSource("findAllByUserIdDataProvider")
    void findAllByUserId(Long userId, int expectedSize, Pageable paging) {
        List<Order> orders = dao.findAllByUserId(userId, paging);
        int actualSize = orders.size();
        Assertions.assertEquals(actualSize, expectedSize);
    }


    public static Object[][] countByUserIdDataProvider() {
        return new Object[][]{
                {1L, 9},
                {99999999L, 0}
        };
    }

    @ParameterizedTest
    @MethodSource("countByUserIdDataProvider")
    void countByUserId(Long userId, int expected) {
        int actual = dao.countByUserId(userId);
        Assertions.assertEquals(expected, actual);
    }

    public static Object[][] findByIdAndByUserIdDataProvider() {
        return new Object[][]{
                {159L, 1L, true},
                {1L, 1L, false}
        };
    }

    @ParameterizedTest
    @MethodSource("findByIdAndByUserIdDataProvider")
    void findByIdAndUserId(Long id, Long userId, boolean expected) {
        Optional<Order> order = dao.findByIdAndUserId(id, userId);
        boolean actual = order.isPresent();
        Assertions.assertEquals(expected, actual);
    }
}