package com.epam.esm.dao.impl;

import com.epam.esm.dao.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class UserDaoImplTest {

    @Autowired
    private UserDaoImpl dao;

    public static Object[][] findByIdDataProvider() {
        return new Object[][]{
                {1L, true},
                {99999999L, false}
        };
    }

    @ParameterizedTest
    @MethodSource("findByIdDataProvider")
    void findById(Long id, boolean expected) {
        Optional<User> user = dao.findById(id);
        boolean actual = user.isPresent();
        Assertions.assertEquals(actual, expected);
    }

    public static Object[][] findByLoginAndPasswordDataProvider() {
        return new Object[][]{
                {"1@gmail.com", "1", true},
                {"1@gmail.com", "2", false},
                {"99999999@gmail.com", "99999999", false}
        };
    }

    @ParameterizedTest
    @MethodSource("findByLoginAndPasswordDataProvider")
    void findByLoginAndPassword(String login, String password, boolean expected) {
        Optional<User> user = dao.findByLoginAndPassword(login, password);
        boolean actual = user.isPresent();
        Assertions.assertEquals(actual, expected);
    }
}