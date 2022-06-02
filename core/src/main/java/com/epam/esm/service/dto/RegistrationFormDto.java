package com.epam.esm.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

/**
 * Class represent user data for registration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationFormDto extends RepresentationModel<RegistrationFormDto> {

    private String username;
    private String name;
    private String password;

}
