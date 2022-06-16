package com.epam.esm.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class represent username and password for authorization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsernamePasswordDto {

    private String username;
    private String password;

}
