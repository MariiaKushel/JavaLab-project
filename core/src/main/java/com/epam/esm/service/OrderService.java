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
     * Find all orders by user id.
     *
     * @param userId user id
     * @param page   page
     * @param size   page size
     * @return Order list as OrderDto list or empty list if no one Order was not found
     * @throws CustomException if userId, page, size have not valid value
     */
    List<OrderDto> findAllByUser(long userId, int page, int size) throws CustomException;

    /**
     * Find the last page by all orders by user id
     *
     * @param userId user id
     * @param size   page size
     * @return last page value
     * @throws CustomException if userId or size have not valid value
     */
    int findAllByUserLastPage(long userId, int size) throws CustomException;

    /**
     * Find Order by id and by user id
     *
     * @param orderId Order id
     * @param userId  user id
     * @return Order as OrderDto
     * @throws CustomException if orderId or userId have not valid value or Order was not found
     */
    OrderDto findByIdAndByUser(long orderId, long userId) throws CustomException;

    /**
     * Create new Order
     *
     * @param userId       user id
     * @param certificates certificate list
     * @return new Order as OrderDto
     * @throws CustomException if id or certificates have not valid value
     * or User or GiftCertificate was not found
     * or GiftCertificate price was changed
     */
    OrderDto create(long userId, List<CertificateDto> certificates) throws CustomException;

}
