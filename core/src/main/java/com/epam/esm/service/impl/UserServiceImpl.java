package com.epam.esm.service.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.entity.User;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.UserDto;
import com.epam.esm.service.validator.CustomValidator;
import com.epam.esm.util.DtoEntityConvector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.epam.esm.exception.CustomErrorCode.RESOURCE_NOT_FOUND;

@Service
public class UserServiceImpl implements UserService {

    private UserDao dao;
    private CustomValidator validator;

    @Autowired
    public UserServiceImpl(UserDao dao, CustomValidator validator) {
        this.dao = dao;
        this.validator = validator;
    }

    @Override
    public UserDto findById(long id) throws CustomException {
        boolean isValid = validator.validateEntityId(id);
        if (!isValid) {
            throw new CustomException("id=" + id, CustomErrorCode.NOT_VALID_DATA);
        }
        Optional<User> userOptional = dao.findById(id);
        User user = userOptional.orElseThrow(() -> new CustomException("id=" + id, RESOURCE_NOT_FOUND));
        return DtoEntityConvector.convert(user);
    }
}
