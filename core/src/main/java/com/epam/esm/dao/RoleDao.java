package com.epam.esm.dao;

import com.epam.esm.dao.entity.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Interface for database operation with Role entity
 */
public interface RoleDao extends CrudRepository<Role, Long> {

    /**
     * Find Role by role name
     * @param name role name
     * @return Optional representation of Role or empty Optional, if Role was not found
     */
    Optional<Role> findByName(String name);
}
