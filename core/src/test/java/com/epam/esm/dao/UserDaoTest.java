package com.epam.esm.dao;

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
class UserDaoTest {

    @Autowired
    private UserDao dao;

    public static Object[][] findByLoginDataProvider() {
        return new Object[][]{
                {"1@gmail.com", true},
                {"unknown@gmail.com", false}
        };
    }

    @ParameterizedTest
    @MethodSource("findByLoginDataProvider")
    void findByLogin(String login, boolean expected) {
        Optional<User> user = dao.findByLogin(login);
        boolean actual = user.isPresent();
        Assertions.assertEquals(expected, actual);
    }
}