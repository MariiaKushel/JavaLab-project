package com.epam.esm.service;

import com.epam.esm.exception.CustomException;
import com.epam.esm.service.dto.TagDto;

import java.util.List;

/**
 * Interface contains service methods for work with CustomTag entity
 */
public interface TagService {

    /**
     * Find CustomTag by id
     *
     * @param id CustomTag id
     * @return CustomTag as TagDto
     * @throws CustomException if id has not valid value or CustomTag was not found
     */
    TagDto findById(long id) throws CustomException;

    /**
     * Find all CustomTags with pagination
     *
     * @param page page
     * @param size page size
     * @return CustomTag list as TagDto list or empty list if no one CustomTag was not found
     */
    List<TagDto> findAll(int page, int size);

    /**
     * Delete CustomTag by id
     *
     * @param id CustomTag id
     * @throws CustomException if id has not valid value or CustomTag not found
     */
    void delete(long id) throws CustomException;

    /**
     * Create new CustomTag
     *
     * @param tag blank Of CustomTag as TagDto
     * @return new CustomTag as TagDto
     * @throws CustomException if id has not valid value or if CustomTag with such name already exist
     */
    TagDto create(TagDto tag) throws CustomException;

    /**
     * Find the most widely tag by user of a user with the highest cost of all orders.
     * If such users more than one, take first of them.
     * If user have more than one the most widely tag, take first of them.
     *
     * @return CustomTag as TagDto
     */
    TagDto findTheMostWidelyTag();


    /**
     * Count all CustomTags
     *
     * @return quantity of CustomTags
     */
    long count();
}
