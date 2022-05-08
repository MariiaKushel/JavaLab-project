package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.dao.entity.Order;
import com.epam.esm.dao.entity.User;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.validator.CustomValidator;
import com.epam.esm.util.DtoEntityConvector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.exception.CustomErrorCode.RESOURCE_NOT_FOUND;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderDao dao;
    private CustomValidator validator;
    private GiftCertificateDao certificateDao;
    private UserDao userDao;

    @Autowired
    public OrderServiceImpl(OrderDao dao, CustomValidator validator,
                            GiftCertificateDao certificateDao, UserDao userDao) {
        this.dao = dao;
        this.validator = validator;
        this.certificateDao = certificateDao;
        this.userDao = userDao;
    }

    @Override
    public OrderDto findById(long id) throws CustomException {
        boolean isValid = validator.validateEntityId(id);
        if (!isValid) {
            throw new CustomException("id=" + id, CustomErrorCode.NOT_VALID_DATA);
        }
        Optional<Order> orderOptional = dao.findById(id);
        Order order = orderOptional.orElseThrow(() -> new CustomException("id=" + id, RESOURCE_NOT_FOUND));
        return DtoEntityConvector.convert(order);
    }

    @Override
    public List<OrderDto> findAllByUser(long userId, int page, int size) throws CustomException {
        boolean isValid = validator.validateEntityId(userId);
        if (!isValid) {
            throw new CustomException("id=" + userId, CustomErrorCode.NOT_VALID_DATA);
        }
        List<Order> orders = dao.findByUser(userId, page, size);
        return DtoEntityConvector.convertOrders(orders);
    }

    @Override
    public OrderDto findByIdAndByUser(long orderId, long userId) throws CustomException {
        boolean isValidOrderId = validator.validateEntityId(orderId);
        boolean isValidUserId = validator.validateEntityId(userId);
        if (!isValidOrderId || !isValidUserId) {
            throw new CustomException("order id=" + orderId + "; user id=" + userId, CustomErrorCode.NOT_VALID_DATA);
        }
        Optional<Order> orderOptional = dao.findByIdAndByUser(orderId, userId);
        Order order = orderOptional.orElseThrow(() -> new CustomException("order id=" + orderId + "; user id=" + userId,
                RESOURCE_NOT_FOUND));
        return DtoEntityConvector.convert(order);
    }

    @Override
    public long countByUser(long userId) throws CustomException {
        boolean isValid = validator.validateEntityId(userId);
        if (!isValid) {
            throw new CustomException("id=" + userId, CustomErrorCode.NOT_VALID_DATA);
        }
        return dao.countByUser(userId);
    }

    @Override
    public OrderDto create(long userId, List<CertificateDto> certificates) throws CustomException {
        List<Long> ids = certificates.stream()
                .map(CertificateDto::getId)
                .toList();
        boolean isValid = ids.stream().allMatch(validator::validateEntityId);
        boolean isValidUserId = validator.validateEntityId(userId);
        if (ids.isEmpty() || !isValid || !isValidUserId) {
            throw new CustomException("user id=" + userId + "or certificate ids=" + ids,
                    CustomErrorCode.NOT_VALID_DATA);
        }
        Optional<User> userOptional = userDao.findById(userId);
        User user = userOptional.orElseThrow(() -> new CustomException("user id=" + userId, RESOURCE_NOT_FOUND));
        List<GiftCertificate> certificateList = new ArrayList<>();
        for (long id : ids) {
            Optional<GiftCertificate> certificateOptional = certificateDao.findById(id);
            GiftCertificate certificate = certificateOptional
                    .orElseThrow(() -> new CustomException("certificate id=" + id, RESOURCE_NOT_FOUND));
            certificateList.add(certificate);
        }
        Order order = new Order();
        order.setUser(user);
        order.setGiftCertificatesList(certificateList);
        BigDecimal amount = certificateList.stream()
                .map(GiftCertificate::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setAmount(amount);
        Order newOrder = dao.save(order);
        return DtoEntityConvector.convert(newOrder);
    }
}
