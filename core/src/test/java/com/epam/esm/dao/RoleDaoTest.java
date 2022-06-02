package com.epam.esm.dao;

import com.epam.esm.dao.entity.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class RoleDaoTest {

    @Autowired
    private RoleDao dao;

    public static Object[][] findByNameDataProvider() {
        return new Object[][]{
                {"ROLE_USER", true},
                {"ROLE_UNKNOWN", false}
        };
    }

    @ParameterizedTest
    @MethodSource("findByNameDataProvider")
    void findByName(String name, boolean expected) {
        Optional<Role> role = dao.findByName(name);
        boolean actual = role.isPresent();
        Assertions.assertEquals(expected, actual);
    }
}