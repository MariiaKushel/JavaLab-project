package com.epam.esm.service.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.entity.User;
import com.epam.esm.enumeration.UserRole;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.RegistrationFormDto;
import com.epam.esm.service.dto.UserDto;
import com.epam.esm.service.validator.CustomValidator;
import com.epam.esm.util.DtoEntityConvector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.epam.esm.exception.CustomErrorCode.NOT_VALID_DATA;
import static com.epam.esm.exception.CustomErrorCode.RESOURCE_NOT_FOUND;

@Service
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private CustomValidator validator;

    @Autowired
    public UserServiceImpl(UserDao userDao, CustomValidator validator) {
        this.userDao = userDao;
        this.validator = validator;
    }

    @Override
    public UserDto findById(long id) throws CustomException {
        boolean isValid = validator.validateEntityId(id);
        if (!isValid) {
            throw new CustomException("id=" + id, NOT_VALID_DATA);
        }
        Optional<User> userOptional = userDao.findById(id);
        User user = userOptional.orElseThrow(() -> new CustomException("id=" + id, RESOURCE_NOT_FOUND));
        return DtoEntityConvector.convert(user);
    }

    @Override
    public UserDto create(RegistrationFormDto registrationForm) throws CustomException {
        String username = registrationForm.getUsername();
        boolean isValidUserName = validator.validateUsername(username);
        if (!isValidUserName) {
            throw new CustomException("username=" + username, NOT_VALID_DATA);
        }
        Optional<User> userOptional = userDao.findByLogin(username);
        if (userOptional.isPresent()) {
            throw new CustomException("username=" + username, CustomErrorCode.RESOURCE_ALREADY_EXIST);
        }
        boolean isValidUserForm = validator.validateRegistrationForm(registrationForm);
        if (!isValidUserForm) {
            throw new CustomException("name=" + registrationForm.getName()
                    + "; password=" + registrationForm.getPassword(), NOT_VALID_DATA);
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String secretPassword = encoder.encode(registrationForm.getPassword());
        registrationForm.setPassword(secretPassword);
        User newUser = DtoEntityConvector.convert(registrationForm);
        newUser.setRole(UserRole.ROLE_USER);
        User user = userDao.save(newUser);
        return DtoEntityConvector.convert(user);
    }

    @Override
    public User findByUsernameForSecurity(String username) throws CustomException {
        boolean isValid = validator.validateUsername(username);
        if (!isValid) {
            throw new CustomException("username=" + username, NOT_VALID_DATA);
        }
        Optional<User> userOptional = userDao.findByLogin(username);
        return userOptional.orElseThrow(() -> new CustomException("username=" + username, RESOURCE_NOT_FOUND));
    }
}
