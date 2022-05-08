package com.epam.esm.dao;

import com.epam.esm.dao.entity.CustomTag;

import java.util.Optional;

/**
 * Interface add BaseDao for work with CustomTag entity
 */
public interface CustomTagDao extends BaseDao<CustomTag, Long> {

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
    CustomTag findTheMostWidelyTags();

}
