package com.epam.esm.service.impl;

import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.dao.entity.Order;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.validator.CustomValidator;
import com.epam.esm.util.DtoEntityConvector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

class CertificateServiceImplTest {

    private GiftCertificateDao daoMock;
    private CustomTagDao tagDaoMock;
    private CustomValidator validatorMock;
    private CertificateService service;

    public CertificateServiceImplTest() {
        this.daoMock = Mockito.mock(GiftCertificateDao.class);
        this.tagDaoMock = Mockito.mock(CustomTagDao.class);
        this.validatorMock = Mockito.mock(CustomValidator.class);
        this.service = new CertificateServiceImpl(daoMock, validatorMock, tagDaoMock);
    }

    @Test
    void findById() throws CustomException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(certificate));
        CertificateDto expected = DtoEntityConvector.convert(certificate);
        CertificateDto actual = service.findById(1L);

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
    void findAll() throws CustomException {
        GiftCertificate certificate1 = new GiftCertificate();
        certificate1.setId(1L);
        GiftCertificate certificate2 = new GiftCertificate();
        certificate1.setId(2L);
        List<GiftCertificate> certificates = List.of(certificate1, certificate2);
        Mockito.when(daoMock.findAll(Mockito.anyInt(), Mockito.anyInt())).thenReturn(certificates);
        List<CertificateDto> expected = DtoEntityConvector.convertCertificates(certificates);
        List<CertificateDto> actual = service.findAll(1, 5);

        Mockito.verify(daoMock, Mockito.times(1)).findAll(Mockito.anyInt(), Mockito.anyInt());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void delete() throws CustomException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(certificate));
        Mockito.doNothing().when(daoMock).delete(Mockito.any());
        service.delete(1L);

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1)).delete(Mockito.any());
    }

    @Test
    void deleteNotFoundException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.delete(999L));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void deleteNotValidException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(false);
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.delete(-1L));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void deleteLinkedToAnotherResourceException() {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        Order order = new Order();
        order.setId(1L);
        certificate.setOrders(Set.of(order));
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(certificate));
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.delete(1L));
        CustomErrorCode expected = CustomErrorCode.LINKED_TO_ANOTHER_RESOURCE;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void create() throws CustomException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        Mockito.when(validatorMock.validateCertificateDtoCreate(Mockito.any())).thenReturn(true);
        CustomTag tag1 = new CustomTag(2L, "existed_tag");//
        Mockito.when(tagDaoMock.findByName("new_tag")).thenReturn(Optional.empty());
        Mockito.when(tagDaoMock.findByName("existed_tag")).thenReturn(Optional.of(tag1));
        CustomTag tag2 = new CustomTag(1L, "old_tag");
        Mockito.when(tagDaoMock.findById(1L)).thenReturn(Optional.of(tag2));
        Mockito.when(daoMock.save(Mockito.any())).thenReturn(certificate);
        Mockito.when(daoMock.findNameAndDescriptionAndPriceAndDuration(Mockito.anyString(), Mockito.anyString(),
                Mockito.any(BigDecimal.class), Mockito.anyInt())).thenReturn(Optional.empty());

        CertificateDto dto = new CertificateDto();
        dto.setName("new certificate");
        dto.setDescription("super gift");
        dto.setPrice(new BigDecimal(500));
        dto.setDuration(30);
        TagDto oldDto = new TagDto(1L, "old_tag");
        TagDto newDto = new TagDto("new_tag");
        TagDto existedDto = new TagDto("existed_tag");
        dto.setTags(Set.of(oldDto, newDto, existedDto));

        CertificateDto expected = DtoEntityConvector.convert(certificate);
        CertificateDto actual = service.create(dto);

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateDtoCreate(Mockito.any());
        Mockito.verify(tagDaoMock, Mockito.times(1)).findByName("new_tag");
        Mockito.verify(tagDaoMock, Mockito.times(1)).findByName("existed_tag");
        Mockito.verify(tagDaoMock, Mockito.times(1)).findById(1L);
        Mockito.verify(daoMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(1))
                .findNameAndDescriptionAndPriceAndDuration(Mockito.anyString(), Mockito.anyString(),
                        Mockito.any(BigDecimal.class), Mockito.anyInt());
        Assertions.assertEquals(actual, expected);
    }

    public static Object[][] createAlreadyExistExceptionDataProvider() {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        Optional<GiftCertificate> certificateOptional = Optional.of(certificate);
        Optional<GiftCertificate> certificateEmpty = Optional.empty();
        Optional<CustomTag> tagOptional = Optional.of(new CustomTag(1L, "tag"));
        return new Object[][]{
                {certificateOptional, tagOptional, 0},
                {certificateEmpty, tagOptional, 1}
        };
    }

    @ParameterizedTest
    @MethodSource("createAlreadyExistExceptionDataProvider")
    void createAlreadyExistException(Optional<GiftCertificate> certificate, Optional<CustomTag> tag, int times) {
        Mockito.when(validatorMock.validateCertificateDtoCreate(Mockito.any())).thenReturn(true);
        Mockito.when(daoMock.findNameAndDescriptionAndPriceAndDuration(Mockito.anyString(), Mockito.anyString(),
                Mockito.any(BigDecimal.class), Mockito.anyInt())).thenReturn(certificate);
        Mockito.when(tagDaoMock.findById(1L)).thenReturn(tag);
        CertificateDto dto = new CertificateDto();
        dto.setName("new certificate");
        dto.setDescription("super gift");
        dto.setPrice(new BigDecimal(500));
        dto.setDuration(30);
        dto.setTags(Set.of(new TagDto(1L, "wrong_tag_name")));

        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.create(dto));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_ALREADY_EXIST;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateDtoCreate(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(1)).findNameAndDescriptionAndPriceAndDuration(
                Mockito.anyString(), Mockito.anyString(), Mockito.any(BigDecimal.class), Mockito.anyInt());
        Mockito.verify(tagDaoMock, Mockito.times(times)).findById(1L);
        Assertions.assertEquals(actual, expected);
    }

    public static Object[][] createNotValidExceptionDataProvider() {
        CertificateDto dto1 = new CertificateDto();
        dto1.setId(1L);

        CertificateDto dto2 = new CertificateDto();

        return new Object[][]{
                {dto1, true},
                {dto2, false},
        };
    }

    @ParameterizedTest
    @MethodSource("createNotValidExceptionDataProvider")
    void createNotValidException(CertificateDto dto, boolean isValid) {
        Mockito.when(validatorMock.validateCertificateDtoCreate(Mockito.any())).thenReturn(isValid);
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.create(dto));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateDtoCreate(Mockito.any());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void createNotFoundException() {
        Mockito.when(validatorMock.validateCertificateDtoCreate(Mockito.any())).thenReturn(true);
        Mockito.when(daoMock.findNameAndDescriptionAndPriceAndDuration(Mockito.anyString(), Mockito.anyString(),
                Mockito.any(BigDecimal.class), Mockito.anyInt())).thenReturn(Optional.empty());
        Mockito.when(tagDaoMock.findById(1L)).thenReturn(Optional.empty());

        CertificateDto dto = new CertificateDto();
        dto.setName("new certificate");
        dto.setDescription("super gift");
        dto.setPrice(new BigDecimal(500));
        dto.setDuration(30);
        dto.setTags(Set.of(new TagDto(1L, "tag")));
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.create(dto));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateDtoCreate(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(1)).findNameAndDescriptionAndPriceAndDuration(
                Mockito.anyString(), Mockito.anyString(), Mockito.any(BigDecimal.class), Mockito.anyInt());
        Mockito.verify(tagDaoMock, Mockito.times(1)).findById(1L);
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void update() throws CustomException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        Mockito.when(validatorMock.validateCertificateDtoUpdate(Mockito.any())).thenReturn(true);
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(certificate));
        Mockito.when(daoMock.updateName(Mockito.anyLong(), Mockito.anyString())).thenReturn(certificate);
        Mockito.when(daoMock.updateDescription(Mockito.anyLong(), Mockito.anyString())).thenReturn(certificate);
        Mockito.when(daoMock.updatePrice(Mockito.anyLong(), Mockito.any(BigDecimal.class))).thenReturn(certificate);
        Mockito.when(daoMock.updateDuration(Mockito.anyLong(), Mockito.anyInt())).thenReturn(certificate);
        Mockito.when(daoMock.updateTags(Mockito.anyLong(), Mockito.anySet())).thenReturn(certificate);

        CertificateDto dto = new CertificateDto();
        dto.setName("new name");
        dto.setDescription("new description");
        dto.setPrice(new BigDecimal("150.00"));
        dto.setDuration(30);
        dto.setTags(new HashSet<>());

        CertificateDto expected = DtoEntityConvector.convert(certificate);
        CertificateDto actual = service.update(1L, dto);

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateDtoUpdate(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1)).updateName(Mockito.anyLong(),
                Mockito.anyString());
        Mockito.verify(daoMock, Mockito.times(1)).updateDescription(Mockito.anyLong(),
                Mockito.anyString());
        Mockito.verify(daoMock, Mockito.times(1)).updatePrice(Mockito.anyLong(),
                Mockito.any(BigDecimal.class));
        Mockito.verify(daoMock, Mockito.times(1)).updateDuration(Mockito.anyLong(),
                Mockito.anyInt());
        Mockito.verify(daoMock, Mockito.times(1)).updateTags(Mockito.anyLong(),
                Mockito.anySet());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void updateNotFoundException() {
        Mockito.when(validatorMock.validateCertificateDtoUpdate(Mockito.any())).thenReturn(true);
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        CertificateDto dto = new CertificateDto();
        dto.setName("new name");
        dto.setDescription("new description");
        dto.setPrice(new BigDecimal("150.00"));
        dto.setDuration(30);
        dto.setTags(new HashSet<>());
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.update(999L, dto));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateDtoUpdate(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void updateNotValidException() {
        Mockito.when(validatorMock.validateCertificateDtoUpdate(Mockito.any())).thenReturn(false);
        CertificateDto dto = new CertificateDto();
        dto.setDuration(-30);
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.update(-1L, dto));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateDtoUpdate(Mockito.any());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void updateName() throws CustomException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        certificate.setName("new name");
        Mockito.when(validatorMock.validateCertificateDtoUpdate(Mockito.any())).thenReturn(true);
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(certificate));
        Mockito.when(daoMock.updateName(Mockito.anyLong(), Mockito.anyString())).thenReturn(certificate);

        CertificateDto expected = DtoEntityConvector.convert(certificate);
        CertificateDto actual = service.updateName(1L, "new name");

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateDtoUpdate(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1)).updateName(Mockito.anyLong(),
                Mockito.anyString());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void updateNameNotFoundException() {
        Mockito.when(validatorMock.validateCertificateDtoUpdate(Mockito.any())).thenReturn(true);
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.updateName(999L, "new name"));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateDtoUpdate(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void updateNameNotValidException() {
        Mockito.when(validatorMock.validateCertificateDtoUpdate(Mockito.any())).thenReturn(false);
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.updateName(-1L, "!!!"));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateDtoUpdate(Mockito.any());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findAllByParameters() throws CustomException {
        GiftCertificate certificate1 = new GiftCertificate();
        certificate1.setId(1L);
        GiftCertificate certificate2 = new GiftCertificate();
        certificate2.setId(2L);
        List<GiftCertificate> certificates = List.of(certificate1, certificate2);
        Mockito.when(validatorMock.validateSearchParameters(Mockito.anyMap())).thenReturn(true);
        Mockito.when(daoMock.findAllByParameters(Mockito.anyMap(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(certificates);

        Map<String, String> param = new HashMap<>();
        param.put("tag", "tag_1");
        List<CertificateDto> expected = DtoEntityConvector.convertCertificates(certificates);
        List<CertificateDto> actual = service.findAllByParameters(param, 1, 10);

        Mockito.verify(validatorMock, Mockito.times(1)).validateSearchParameters(Mockito.anyMap());
        Mockito.verify(daoMock, Mockito.times(1)).findAllByParameters(Mockito.anyMap(),
                Mockito.anyInt(), Mockito.anyInt());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findAllByParametersNotValidException() {
        Mockito.when(validatorMock.validateSearchParameters(Mockito.anyMap())).thenReturn(false);
        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findAllByParameters(new HashMap<>(), 2, 10));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateSearchParameters(Mockito.anyMap());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findByTags() throws CustomException {
        GiftCertificate certificate1 = new GiftCertificate();
        certificate1.setId(1L);
        GiftCertificate certificate2 = new GiftCertificate();
        certificate2.setId(2L);
        List<GiftCertificate> certificates = List.of(certificate1, certificate2);
        Mockito.when(validatorMock.validateTagDto(Mockito.any())).thenReturn(true);
        Mockito.when(daoMock.findByTags(Mockito.anyInt(), Mockito.anyInt(), Mockito.any())).thenReturn(certificates);

        List<CertificateDto> expected = DtoEntityConvector.convertCertificates(certificates);
        List<CertificateDto> actual = service.findByTags(new String[]{"tag_1", "tag_2"}, 1, 10);

        Mockito.verify(validatorMock, Mockito.times(2)).validateTagDto(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(1))
                .findByTags(Mockito.anyInt(), Mockito.anyInt(), Mockito.any());
        Assertions.assertEquals(actual, expected);
    }


    @Test
    void findByTagsNotValidException() {
        Mockito.when(validatorMock.validateTagDto(Mockito.any())).thenReturn(false);
        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findByTags(new String[]{"tag!!!"}, 2, 10));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateTagDto(Mockito.any());
        Assertions.assertEquals(actual, expected);
    }
}