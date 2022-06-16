package com.epam.esm.dao;

import com.epam.esm.dao.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface for database operation with Order entity
 */
public interface OrderDao extends PagingAndSortingRepository<Order, Long> {

    /**
     * Find Orders by User id with pagination
     *
     * @param userId User id
     * @param paging pagination parameters
     * @return list of Orders or empty list if no one Order was not found
     */
    List<Order> findAllByUserId(Long userId, Pageable paging);

    /**
     * Count all Orders by User id
     *
     * @param userId User id
     * @return Orders quantity
     */
    int countByUserId(Long userId);

    /**
     * Find Order by id and User id
     *
     * @param id     Order id
     * @param userId User id
     * @return Optional representation of Order or empty Optional, if Order was not found
     */
    Optional<Order> findByIdAndUserId(Long id, Long userId);

}