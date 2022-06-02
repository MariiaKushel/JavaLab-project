package com.epam.esm.controller;

import com.epam.esm.exception.CustomException;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.RegistrationFormDto;
import com.epam.esm.service.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller represent operation on User and their Orders
 */
@RestController
@RequestMapping(value = "/registration")
public class RegistrationController {

    private UserService service;

    @Autowired
    public RegistrationController(UserService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus (HttpStatus.CREATED)
    public UserDto createUser (@RequestBody RegistrationFormDto form) throws CustomException {
        return service.create(form);
    }

}
