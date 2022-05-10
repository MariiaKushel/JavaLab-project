package com.epam.esm.dao;

import com.epam.esm.dao.entity.Order;

import java.util.List;
import java.util.Optional;

/**
 * Interface add BaseDao for work with Order entity
 */
public interface OrderDao extends BaseDao<Order, Long> {

    /**
     * Find Orders by User id with pagination
     *
     * @param page page
     * @param size page size
     * @return list of Orders or empty list if no one Order was not found
     */
    List<Order> findByUser(Long userId, int page, int size);

    /**
     * Find Order by id and User id
     *
     * @param orderId Order id
     * @param userId  User id
     * @return Optional representation of Order or empty Optional, if Order was not found
     */
    Optional<Order> findByIdAndByUser(Long orderId, Long userId);

    /**
     * Count all Orders by User id
     *
     * @return quantity of all Orders by User id
     */
    long countByUser(Long userId);
}