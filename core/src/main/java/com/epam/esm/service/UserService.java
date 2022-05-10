package com.epam.esm.service;

import com.epam.esm.exception.CustomException;
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
     * @throws CustomException if id has not valid value User was not found
     */
    UserDto findById(long id) throws CustomException;

}
