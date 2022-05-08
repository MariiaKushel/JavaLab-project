package com.epam.esm.dao;

import com.epam.esm.dao.entity.User;

import java.util.Optional;

/**
 * Interface add BaseDao for work with Order entity
 */
public interface UserDao extends BaseDao<User, Long> {
    /**
     * Find User by login and password
     *
     * @param login    user login
     * @param password user password
     * @return Optional representation of User or empty Optional, if User was not found
     */
    Optional<User> findByLoginAndPassword(String login, String password);

}
