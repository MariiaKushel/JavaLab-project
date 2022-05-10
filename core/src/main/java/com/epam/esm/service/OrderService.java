package com.epam.esm.service;

import com.epam.esm.exception.CustomException;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.OrderDto;

import java.util.List;

/**
 * Interface contains service methods for work with Order entity
 */
public interface OrderService {

    /**
     * Find Order by id
     *
     * @param id Order id
     * @return Order as TagDto
     * @throws CustomException if id has not valid value or Order was not found
     */
    OrderDto findById(long id) throws CustomException;

    /**
     * @param userId user id
     * @param page   page
     * @param size   page size
     * @return Order list as OrderDto list or empty list if no one Order was not found
     * @throws CustomException
     */
    List<OrderDto> findAllByUser(long userId, int page, int size) throws CustomException;

    /**
     * Find Order by id and by user id
     *
     * @param orderId Order id
     * @param userId  user id
     * @return Order as OrderDto
     * @throws CustomException if id has not valid value or Order was not found
     */
    OrderDto findByIdAndByUser(long orderId, long userId) throws CustomException;

    /**
     * Count Order by user id
     *
     * @param userId user id
     * @return quantity of Order
     * @throws CustomException
     */
    long countByUser(long userId) throws CustomException;

    /**
     * Create new Order
     *
     * @param userId       user id
     * @param certificates certificate list
     * @return new Order as OrderDto
     * @throws CustomException if id or certificates has not valid value
     */
    OrderDto create(long userId, List<CertificateDto> certificates) throws CustomException;

}
