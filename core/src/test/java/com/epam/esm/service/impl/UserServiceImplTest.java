package com.epam.esm.service.impl;

import com.epam.esm.dao.RoleDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.entity.Role;
import com.epam.esm.dao.entity.User;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.RegistrationFormDto;
import com.epam.esm.service.dto.UserDto;
import com.epam.esm.service.validator.CustomValidator;
import com.epam.esm.util.DtoEntityConvector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

class UserServiceImplTest {

    private UserDao userDaoMock;
    private RoleDao roleDaoMock;
    private CustomValidator validatorMock;
    private UserService service;

    public UserServiceImplTest() {
        this.userDaoMock = Mockito.mock(UserDao.class);
        this.roleDaoMock = Mockito.mock(RoleDao.class);
        this.validatorMock = Mockito.mock(CustomValidator.class);
        this.service = new UserServiceImpl(userDaoMock, roleDaoMock, validatorMock);
    }

    @Test
    void findById() throws CustomException {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        User user = new User();
        user.setLogin("ivan@gmail.com");
        user.setPassword("ivan_password");
        user.setName("Ivan");
        user.setRole(role);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userDaoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        UserDto expected = DtoEntityConvector.convert(user);
        UserDto actual = service.findById(1L);

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(userDaoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findByIdNotFoundException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userDaoMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.findById(999L));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(userDaoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findByIdNotValidException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(false);
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.findById(-1L));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void create() throws CustomException {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        User user = new User();
        user.setLogin("ivan@gmail.com");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String secretPassword = encoder.encode("ivan_password");
        user.setPassword(secretPassword);
        user.setName("Ivan");
        user.setRole(role);
        Mockito.when(validatorMock.validateUsername(Mockito.anyString())).thenReturn(true);
        Mockito.when(userDaoMock.findByLogin(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(validatorMock.validateRegistrationForm(Mockito.any())).thenReturn(true);
        Mockito.when(roleDaoMock.findByName(Mockito.anyString())).thenReturn(Optional.of(role));
        Mockito.when(userDaoMock.save(Mockito.any())).thenReturn(user);

        RegistrationFormDto form =
                new RegistrationFormDto("ivan@gmail.com", "Ivan", "ivan_password");
        UserDto expected = DtoEntityConvector.convert(user);
        UserDto actual = service.create(form);

        Mockito.verify(validatorMock, Mockito.times(1)).validateUsername(Mockito.anyString());
        Mockito.verify(userDaoMock, Mockito.times(1)).findByLogin(Mockito.anyString());
        Mockito.verify(validatorMock, Mockito.times(1)).validateRegistrationForm(Mockito.any());
        Mockito.verify(roleDaoMock, Mockito.times(1)).findByName(Mockito.anyString());
        Mockito.verify(userDaoMock, Mockito.times(1)).save(Mockito.any());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createNotValidExceptionUsername() {
        Mockito.when(validatorMock.validateUsername(Mockito.anyString())).thenReturn(false);

        RegistrationFormDto form =
                new RegistrationFormDto("not_valid_username!!!", "Ivan", "ivan_password");
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.create(form));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateUsername(Mockito.anyString());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createNotValidExceptionRegistrationForm() {
        Mockito.when(validatorMock.validateUsername(Mockito.anyString())).thenReturn(true);
        Mockito.when(userDaoMock.findByLogin(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(validatorMock.validateRegistrationForm(Mockito.any())).thenReturn(false);

        RegistrationFormDto form =
                new RegistrationFormDto("ivan@gmail.com", "<Wrong name>", "<wrong_password>");
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.create(form));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateUsername(Mockito.anyString());
        Mockito.verify(userDaoMock, Mockito.times(1)).findByLogin(Mockito.anyString());
        Mockito.verify(validatorMock, Mockito.times(1)).validateRegistrationForm(Mockito.any());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createAlreadyExistException() {
        User user = new User();
        Mockito.when(validatorMock.validateUsername(Mockito.anyString())).thenReturn(true);
        Mockito.when(userDaoMock.findByLogin(Mockito.anyString())).thenReturn(Optional.of(user));

        RegistrationFormDto form =
                new RegistrationFormDto("ivan@gmail.com", "Ivan", "ivan_password");
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.create(form));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_ALREADY_EXIST;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateUsername(Mockito.anyString());
        Mockito.verify(userDaoMock, Mockito.times(1)).findByLogin(Mockito.anyString());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findByUsernameForSecurity() throws CustomException {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        User user = new User();
        user.setLogin("ivan@gmail.com");
        user.setPassword("ivan_password");
        user.setName("Ivan");
        user.setRole(role);
        Mockito.when(validatorMock.validateUsername(Mockito.anyString())).thenReturn(true);
        Mockito.when(userDaoMock.findByLogin(Mockito.anyString())).thenReturn(Optional.of(user));
        User actual = service.findByUsernameForSecurity("ivan@gmail.com");

        Mockito.verify(validatorMock, Mockito.times(1)).validateUsername(Mockito.anyString());
        Mockito.verify(userDaoMock, Mockito.times(1)).findByLogin(Mockito.anyString());
        Assertions.assertEquals(user, actual);
    }

    @Test
    void findByUsernameForSecurityNotFoundException() {
        Mockito.when(validatorMock.validateUsername(Mockito.anyString())).thenReturn(true);
        Mockito.when(userDaoMock.findByLogin(Mockito.anyString())).thenReturn(Optional.empty());
        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findByUsernameForSecurity("not_found_username@gmail.com"));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateUsername(Mockito.anyString());
        Mockito.verify(userDaoMock, Mockito.times(1)).findByLogin(Mockito.anyString());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findByUsernameForSecurityNotValidException() {
        Mockito.when(validatorMock.validateUsername(Mockito.anyString())).thenReturn(false);
        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findByUsernameForSecurity("not_valid_username"));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateUsername(Mockito.anyString());
        Assertions.assertEquals(expected, actual);
    }
}