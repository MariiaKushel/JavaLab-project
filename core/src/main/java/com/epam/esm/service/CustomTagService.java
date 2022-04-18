package com.epam.esm.service;

import com.epam.esm.exception.CustomException;
import com.epam.esm.dao.entity.CustomTag;

import java.util.List;
import java.util.Optional;

/**
 * Interface contains service methods for work with CustomTag entity
 */
public interface CustomTagService {

    /**
     * Find CustomTag by id
     * @param id CustomTag id
     * @return Optional representation of CustomTag or empty Optional, if CustomTag was not found
     * @throws CustomException if id has not valid value
     */
    Optional<CustomTag> findById(long id) throws CustomException;

    /**
     * Find all CustomTags
     * @return list of CustomTags or empty list if no one CustomTag was not found
     */
    List<CustomTag> findAll();

    /**
     * Delete CustomTag by id
     * @param id CustomTag id
     * @throws CustomException if id has not valid value
     */
    void delete (long id) throws CustomException;

    /**
     * Create new CustomTag
     * @param tag blank Of CustomTag
     * @return new CustomTag
     * @throws CustomException if id has not valid value or if CustomTag with such name already exist
     */
    CustomTag create(CustomTag tag) throws CustomException;
}
