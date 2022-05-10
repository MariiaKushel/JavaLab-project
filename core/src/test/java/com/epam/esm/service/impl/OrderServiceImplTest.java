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

import java.math.BigDecimal;
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
    void findById() throws CustomException {
        Order order = new Order();
        order.setId(1L);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(order));
        OrderDto expected = DtoEntityConvector.convert(order);
        OrderDto actual = service.findById(1L);

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findByIdNotFoundException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.findById(999L));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findByIdNotValidException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(false);
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.findById(-1L));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findAllByUser() throws CustomException {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(1L);
        List<Order> orders = List.of(order1, order2);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(orders);
        List<OrderDto> expected = DtoEntityConvector.convertOrders(orders);
        List<OrderDto> actual = service.findAllByUser(1L, 1, 5);

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1))
                .findByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findAllByUserNotValidException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(false);
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.findById(-1L));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findByIdAndByUser() throws CustomException {
        Order order = new Order();
        order.setId(1L);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(order));
        OrderDto expected = DtoEntityConvector.convert(order);
        OrderDto actual = service.findByIdAndByUser(1L, 1L);

        Mockito.verify(validatorMock, Mockito.times(2)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1))
                .findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findByIdAndByUserNotFoundException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.empty());
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.findByIdAndByUser(1L, 1L));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(2)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1))
                .findByIdAndByUser(Mockito.anyLong(), Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findByIdAndByUserNotValidException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(false);
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.findByIdAndByUser(-1L, -1L));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(2)).validateEntityId(Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void create() throws CustomException {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        CertificateDto dto2 = new CertificateDto();
        dto2.setId(2L);
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
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userDaoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(certificateDaoMock.findById(1L)).thenReturn(Optional.of(certificate1));
        Mockito.when(certificateDaoMock.findById(2L)).thenReturn(Optional.of(certificate2));
        Mockito.when(daoMock.save(Mockito.any())).thenReturn(order);

        OrderDto actual = service.create(35L, dtos);
        OrderDto expected = DtoEntityConvector.convert(order);

        Mockito.verify(validatorMock, Mockito.times(dtos.size() + 1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(userDaoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(certificateDaoMock, Mockito.times(1)).findById(1L);
        Mockito.verify(certificateDaoMock, Mockito.times(1)).findById(2L);
        Mockito.verify(daoMock, Mockito.times(1)).save(Mockito.any());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void createNotValidException() {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        CertificateDto dto2 = new CertificateDto();
        dto2.setId(2L);
        List<CertificateDto> dtos = List.of(dto1, dto2);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.create(35L, dtos));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(dtos.size())).validateEntityId(Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void createNotFoundException1() {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        CertificateDto dto2 = new CertificateDto();
        dto2.setId(2L);
        List<CertificateDto> dtos = List.of(dto1, dto2);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userDaoMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.create(35L, dtos));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(dtos.size() + 1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(userDaoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void createNotFoundException2() {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);
        CertificateDto dto2 = new CertificateDto();
        dto2.setId(2L);
        List<CertificateDto> dtos = List.of(dto1, dto2);
        User user = new User();
        user.setId(35L);
        GiftCertificate certificate1 = new GiftCertificate();
        certificate1.setId(1L);
        certificate1.setPrice(new BigDecimal("200.00"));
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userDaoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(certificateDaoMock.findById(1L)).thenReturn(Optional.of(certificate1));
        Mockito.when(certificateDaoMock.findById(2L)).thenReturn(Optional.empty());

        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.create(35L, dtos));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(dtos.size() + 1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(userDaoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(certificateDaoMock, Mockito.times(1)).findById(1L);
        Mockito.verify(certificateDaoMock, Mockito.times(1)).findById(2L);
        Assertions.assertEquals(actual, expected);
    }
}