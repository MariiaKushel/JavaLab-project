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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class OrderServiceImplTest {

    private OrderDao daoMock;
    private CustomValidator validatorMock;
    private GiftCertificateDao certificateDaoMock;
    private UserDao userDaoMock;
    private OrderService service;

    public OrderServiceImplTest() {
        this.daoMock = Mockito.mock(OrderDao.class);
        this.validatorMock = Mockito.mock(CustomValidator.class);
        this.certificateDaoMock = Mockito.mock(GiftCertificateDao.class);
        this.userDaoMock = Mockito.mock(UserDao.class);
        this.service = new OrderServiceImpl(daoMock, validatorMock, certificateDaoMock, userDaoMock);
    }

    @Test
    void findAllByUser() throws CustomException {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(1L);
        List<Order> orders = List.of(order1, order2);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(validatorMock.validatePageSize(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
        Mockito.when(daoMock.findAllByUserId(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(orders);
        List<OrderDto> expected = DtoEntityConvector.convertOrders(orders);
        List<OrderDto> actual = service.findAllByUser(1L, 1, 5);

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(validatorMock, Mockito.times(1))
                .validatePageSize(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(daoMock, Mockito.times(1))
                .findAllByUserId(Mockito.anyLong(), Mockito.any(Pageable.class));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllByUserNotValidException1() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findAllByUser(-1L, 1, 5));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllByUserNotValidException2() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(validatorMock.validatePageSize(Mockito.anyInt(), Mockito.anyInt())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findAllByUser(1L, -1, -5));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(validatorMock, Mockito.times(1))
                .validatePageSize(Mockito.anyInt(), Mockito.anyInt());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllByUserLastPage() throws CustomException {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.countByUserId(Mockito.anyLong())).thenReturn(101);

        int expected = 11;
        int actual = service.findAllByUserLastPage(1L, 10);

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1))
                .countByUserId(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllByUserLastPageNotValidException1() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findAllByUserLastPage(-1L, 5));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllByUserLastPageNotValidException2() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findAllByUserLastPage(1L, -5));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findByIdAndByUser() throws CustomException {
        Order order = new Order();
        order.setId(1L);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findByIdAndUserId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(order));

        OrderDto expected = DtoEntityConvector.convert(order);
        OrderDto actual = service.findByIdAndByUser(1L, 1L);

        Mockito.verify(validatorMock, Mockito.times(2)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1))
                .findByIdAndUserId(Mockito.anyLong(), Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findByIdAndByUserNotFoundException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findByIdAndUserId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.empty());

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findByIdAndByUser(1L, 1L));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(2)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1))
                .findByIdAndUserId(Mockito.anyLong(), Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findByIdAndByUserNotValidException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findByIdAndByUser(-1L, -1L));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(2)).validateEntityId(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void create() throws CustomException {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        dto1.setPrice(new BigDecimal("200.00"));
        CertificateDto dto2 = new CertificateDto();
        dto2.setId(2L);
        dto2.setPrice(new BigDecimal("333.00"));
        List<CertificateDto> dtos = List.of(dto1, dto2);
        User user = new User();
        user.setId(35L);
        GiftCertificate certificate1 = new GiftCertificate();
        certificate1.setId(1L);
        certificate1.setPrice(new BigDecimal("200.00"));
        GiftCertificate certificate2 = new GiftCertificate();
        certificate2.setId(2L);
        certificate2.setPrice(new BigDecimal("333.00"));
        Order order = new Order();
        order.setId(42L);
        order.setAmount(new BigDecimal("533.00"));
        Mockito.when(validatorMock.validateCertificateList(Mockito.anyList())).thenReturn(true);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userDaoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(certificateDaoMock.findByIdAndActive(1L, true)).thenReturn(Optional.of(certificate1));
        Mockito.when(certificateDaoMock.findByIdAndActive(2L, true)).thenReturn(Optional.of(certificate2));
        Mockito.when(daoMock.save(Mockito.any(Order.class))).thenReturn(order);

        OrderDto actual = service.create(35L, dtos);
        OrderDto expected = DtoEntityConvector.convert(order);

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateList(Mockito.anyList());
        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(userDaoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(certificateDaoMock, Mockito.times(1)).findByIdAndActive(1L, true);
        Mockito.verify(certificateDaoMock, Mockito.times(1)).findByIdAndActive(2L, true);
        Mockito.verify(daoMock, Mockito.times(1)).save(Mockito.any());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createNotValidException1() {
        Mockito.when(validatorMock.validateCertificateList(Mockito.anyList())).thenReturn(false);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.create(35L, new ArrayList<>()));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateList(Mockito.anyList());
        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createNotValidException2() {
        Mockito.when(validatorMock.validateCertificateList(Mockito.anyList())).thenReturn(true);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.create(-35L, new ArrayList<>()));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateList(Mockito.anyList());
        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createNotFoundException1() {
        Mockito.when(validatorMock.validateCertificateList(Mockito.anyList())).thenReturn(true);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userDaoMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.create(35L, new ArrayList<>()));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateList(Mockito.anyList());
        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(userDaoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createNotFoundException2() {
        CertificateDto dto = new CertificateDto();
        dto.setId(-1L);
        User user = new User();
        user.setId(35L);
        Mockito.when(validatorMock.validateCertificateList(Mockito.anyList())).thenReturn(true);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userDaoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(certificateDaoMock.findByIdAndActive(Mockito.anyLong(), Mockito.eq(true)))
                .thenReturn(Optional.empty());

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.create(35L, List.of(dto)));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateList(Mockito.anyList());
        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(userDaoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(certificateDaoMock, Mockito.times(1))
                .findByIdAndActive(Mockito.anyLong(), Mockito.eq(true));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createDifferentConditionException() {
        CertificateDto dto = new CertificateDto();
        dto.setId(1L);
        dto.setPrice(new BigDecimal("100.00"));
        User user = new User();
        user.setId(35L);
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        certificate.setPrice(new BigDecimal("200.00"));
        Mockito.when(validatorMock.validateCertificateList(Mockito.anyList())).thenReturn(true);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userDaoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(certificateDaoMock.findByIdAndActive(Mockito.anyLong(), Mockito.eq(true)))
                .thenReturn(Optional.of(certificate));

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.create(35L, List.of(dto)));
        CustomErrorCode expected = CustomErrorCode.DIFFERENT_CONDITION;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateList(Mockito.anyList());
        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(userDaoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(certificateDaoMock, Mockito.times(1))
                .findByIdAndActive(Mockito.anyLong(), Mockito.eq(true));
        Assertions.assertEquals(expected, actual);
    }
}