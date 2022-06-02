package com.epam.esm.service.dto;

import com.epam.esm.enumeration.AppRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

/**
 * Class represent User entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "users")
public class UserDto extends RepresentationModel<UserDto> {

    private Long id;
    private String username;
    private String name;
    private AppRole role;

}
