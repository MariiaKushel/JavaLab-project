package com.epam.esm.dao;

import com.epam.esm.dao.entity.CustomTag;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * Interface for database operation with CustomTag entity
 */
public interface CustomTagDao extends PagingAndSortingRepository<CustomTag, Long> {

    /**
     * Find CustomTag by name
     *
     * @param name CustomTag name
     * @return Optional representation of CustomTag or empty Optional, if CustomTag was not found
     */
    Optional<CustomTag> findByName(String name);

    /**
     * Find the most widely tag by user of a user with the highest cost of all orders.
     * If such users more than one, take first of them.
     * If user have more than one the most widely tag, take first of them.
     *
     * @return entity CustomTag
     */
    CustomTag findTheMostWidelyTag();

}
