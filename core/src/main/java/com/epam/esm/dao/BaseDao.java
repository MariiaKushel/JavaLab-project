package com.epam.esm.dao;

import com.epam.esm.dao.entity.BaseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface represent common database operation
 * @param <T> database entity
 * @param <K> entity id
 */
public interface BaseDao<T extends BaseEntity, K> {

    /**
     * Save entity into database
     * @param entity entity to save
     * @return entity after saving
     */
    T save(T entity);

    /**
     * Partly update entity into database
     * @param parameters parameters which must be updated
     * @return entity after updating
     */
    T update(Map<String, String> parameters);

    /**
     * Delete entity into database by id
     * @param id entity id
     */
    void delete(K id);

    /**
     * Find all entity into database
     * @return list of entities or empty list if no one entity was not found
     */
    List<T> findAll();

    /**
     * Find entity into database by id
     * @param id entity id
     * @return Optional representation of entity or empty Optional, if entity was not found
     */
    Optional<T> findById(K id);

}
