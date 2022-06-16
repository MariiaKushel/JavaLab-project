package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.dao.entity.Order;
import com.epam.esm.dao.entity.User;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.validator.CustomValidator;
import com.epam.esm.util.DtoEntityConvector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.exception.CustomErrorCode.DIFFERENT_CONDITION;
import static com.epam.esm.exception.CustomErrorCode.NOT_VALID_DATA;
import static com.epam.esm.exception.CustomErrorCode.RESOURCE_NOT_FOUND;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderDao orderDao;
    private CustomValidator validator;
    private GiftCertificateDao certificateDao;
    private UserDao userDao;

    @Autowired
    public OrderServiceImpl(OrderDao orderDao, CustomValidator validator,
                            GiftCertificateDao certificateDao, UserDao userDao) {
        this.orderDao = orderDao;
        this.validator = validator;
        this.certificateDao = certificateDao;
        this.userDao = userDao;
    }

    @Override
    public List<OrderDto> findAllByUser(long userId, int page, int size) throws CustomException {
        boolean isValidId = validator.validateEntityId(userId);
        if (!isValidId) {
            throw new CustomException("id=" + userId, NOT_VALID_DATA);
        }
        boolean isValidPageSize = validator.validatePageSize(page, size);
        if (!isValidPageSize) {
            throw new CustomException("page=" + page + "; size=" + size, NOT_VALID_DATA);
        }
        Pageable paging = PageRequest.of(page - 1, size);
        List<Order> orders = orderDao.findAllByUserId(userId, paging);
        return DtoEntityConvector.convertOrders(orders);
    }

    @Override
    public int findAllByUserLastPage(long userId, int size) throws CustomException {
        boolean isValidId = validator.validateEntityId(userId);
        if (!isValidId) {
            throw new CustomException("id=" + userId, NOT_VALID_DATA);
        }
        if (size < 1) {
            throw new CustomException("size=" + size, NOT_VALID_DATA);
        }
        int quantity = orderDao.countByUserId(userId);
        int lastPage = quantity % size == 0
                ? quantity / size
                : quantity / size + 1;
        return lastPage;
    }

    @Override
    public OrderDto findByIdAndByUser(long orderId, long userId) throws CustomException {
        boolean isValidOrderId = validator.validateEntityId(orderId);
        boolean isValidUserId = validator.validateEntityId(userId);
        if (!isValidOrderId || !isValidUserId) {
            throw new CustomException("order id=" + orderId + "; user id=" + userId, NOT_VALID_DATA);
        }
        Optional<Order> orderOptional = orderDao.findByIdAndUserId(orderId, userId);
        Order order = orderOptional.orElseThrow(() -> new CustomException("order id=" + orderId + "; user id=" + userId,
                RESOURCE_NOT_FOUND));
        return DtoEntityConvector.convert(order);
    }

    @Override
    public OrderDto create(long userId, List<CertificateDto> certificates) throws CustomException {
        boolean isValidCertificates = validator.validateCertificateList(certificates);
        boolean isValidUserId = validator.validateEntityId(userId);
        if (!isValidCertificates || !isValidUserId) {
            List<String> certificatesForErrorMessage = certificates.stream()
                    .map(c -> "id=" + c.getId() + ", price=" + c.getPrice())
                    .toList();
            throw new CustomException("user id=" + userId + " or certificates=" + certificatesForErrorMessage,
                    NOT_VALID_DATA);
        }
        Optional<User> userOptional = userDao.findById(userId);
        User user = userOptional.orElseThrow(() -> new CustomException("user id=" + userId, RESOURCE_NOT_FOUND));
        List<GiftCertificate> certificatesForOrder = new ArrayList<>();
        for (CertificateDto dto : certificates) {
            Optional<GiftCertificate> certificateOptional = certificateDao.findByIdAndActive(dto.getId(), true);
            GiftCertificate certificate = certificateOptional
                    .orElseThrow(() -> new CustomException("certificate id=" + dto.getId(), RESOURCE_NOT_FOUND));
            if (!dto.getPrice().equals(certificate.getPrice())) {
                throw new CustomException("certificate id=" + dto.getId() + "; old price=" + dto.getPrice()
                        + "; new price=" + certificate.getPrice(), DIFFERENT_CONDITION);
            }
            certificatesForOrder.add(certificate);
        }
        Order order = new Order();
        order.setUser(user);
        order.setGiftCertificatesList(certificatesForOrder);
        BigDecimal amount = certificatesForOrder.stream()
                .map(GiftCertificate::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setAmount(amount);
        Order newOrder = orderDao.save(order);
        return DtoEntityConvector.convert(newOrder);
    }
}
