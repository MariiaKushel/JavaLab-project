package com.epam.esm.service;

import com.epam.esm.dao.entity.User;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.dto.RegistrationFormDto;
import com.epam.esm.service.dto.UserDto;

/**
 * Interface contains service methods for work with User entity
 */
public interface UserService {

    /**
     * Find User by id
     *
     * @param id User id
     * @return User as UserDto
     * @throws CustomException if id has not valid value or User was not found
     */
    UserDto findById(long id) throws CustomException;

    /**
     * Create new User
     *
     * @param registrationForm user registration data
     * @return User as UserDto
     * @throws CustomException if user data has not valid value or such User already exist.
     */
    UserDto create(RegistrationFormDto registrationForm) throws CustomException;

    /**
     * Find User by username
     *
     * @param username user name
     * @return User
     * @throws CustomException if id has not valid value or User was not found
     */
    User findByUsernameForSecurity(String username) throws CustomException;
}
