package com.epam.esm.dao;

import com.epam.esm.dao.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Interface for database operation with User entity
 */
public interface UserDao extends CrudRepository<User, Long> {

    /**
     * Find User by login
     * @param login user login
     * @return Optional representation of User or empty Optional, if User was not found
     */
    Optional<User> findByLogin(String login);
}
