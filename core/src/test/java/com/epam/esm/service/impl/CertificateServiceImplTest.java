package com.epam.esm.service.impl;

import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.dao.entity.Order;
import com.epam.esm.enumeration.SearchParameterName;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.validator.CustomValidator;
import com.epam.esm.util.DtoEntityConvector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.HashMap;
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
        Mockito.when(daoMock.findByIdAndActive(Mockito.anyLong(), Mockito.eq(true)))
                .thenReturn(Optional.of(certificate));

        CertificateDto expected = DtoEntityConvector.convert(certificate);
        CertificateDto actual = service.findById(1L);

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1))
                .findByIdAndActive(Mockito.anyLong(), Mockito.eq(true));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findByIdNotFoundException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findByIdAndActive(Mockito.anyLong(), Mockito.eq(true)))
                .thenReturn(Optional.empty());

        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.findById(999L));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1))
                .findByIdAndActive(Mockito.anyLong(), Mockito.eq(true));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findByIdNotValidException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.findById(-1L));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAll() throws CustomException {
        GiftCertificate certificate1 = new GiftCertificate();
        certificate1.setId(1L);
        GiftCertificate certificate2 = new GiftCertificate();
        certificate1.setId(2L);
        Page<GiftCertificate> certificates = new PageImpl<>(List.of(certificate1, certificate2));
        Mockito.when(validatorMock.validatePageSize(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
        Mockito.when(daoMock.findAllByActive(Mockito.eq(true), Mockito.any(Pageable.class)))
                .thenReturn(certificates);

        List<CertificateDto> expected = DtoEntityConvector.convertCertificates(certificates.toList());
        List<CertificateDto> actual = service.findAll(1, 5);

        Mockito.verify(validatorMock, Mockito.times(1))
                .validatePageSize(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(daoMock, Mockito.times(1))
                .findAllByActive(Mockito.eq(true), Mockito.any(Pageable.class));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllNotValidException() {
        Mockito.when(validatorMock.validatePageSize(Mockito.anyInt(), Mockito.anyInt())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findAll(-1, -5));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1))
                .validatePageSize(Mockito.anyInt(), Mockito.anyInt());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void deleteAsFullDelete() throws CustomException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findByIdAndActive(Mockito.anyLong(), Mockito.eq(true)))
                .thenReturn(Optional.of(certificate));
        Mockito.doNothing().when(daoMock).delete(Mockito.any());
        Mockito.when(daoMock.save(Mockito.any(GiftCertificate.class))).thenReturn(Mockito.any(GiftCertificate.class));

        service.delete(1L);

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1))
                .findByIdAndActive(Mockito.anyLong(), Mockito.eq(true));
        Mockito.verify(daoMock, Mockito.times(1)).delete(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(0)).save(Mockito.any(GiftCertificate.class));
    }

    @Test
    void deleteAsMakeNonActive() throws CustomException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        Order order = new Order();
        order.setId(1L);
        certificate.setOrders(Set.of(order));
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findByIdAndActive(Mockito.anyLong(), Mockito.eq(true)))
                .thenReturn(Optional.of(certificate));
        Mockito.doNothing().when(daoMock).delete(Mockito.any());
        Mockito.when(daoMock.save(Mockito.any(GiftCertificate.class))).thenReturn(Mockito.any(GiftCertificate.class));

        service.delete(1L);

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1))
                .findByIdAndActive(Mockito.anyLong(), Mockito.eq(true));
        Mockito.verify(daoMock, Mockito.times(0)).delete(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void deleteNotFoundException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findByIdAndActive(Mockito.anyLong(), Mockito.eq(true)))
                .thenReturn(Optional.empty());

        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.delete(999L));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1))
                .findByIdAndActive(Mockito.anyLong(), Mockito.eq(true));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void deleteNotValidException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.delete(-1L));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void create() throws CustomException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        CustomTag tag1 = new CustomTag(2L, "existed_tag");//
        CustomTag tag2 = new CustomTag(1L, "old_tag");
        Mockito.when(validatorMock.validateCertificateDtoCreate(Mockito.any())).thenReturn(true);
        Mockito.when(daoMock.findByNameAndDescriptionAndPriceAndDurationAndActive(Mockito.anyString(), Mockito.anyString(),
                        Mockito.any(BigDecimal.class), Mockito.anyInt(), Mockito.eq(true)))
                .thenReturn(Optional.empty());
        Mockito.when(tagDaoMock.findByName("new_tag")).thenReturn(Optional.empty());
        Mockito.when(tagDaoMock.findByName("existed_tag")).thenReturn(Optional.of(tag1));
        Mockito.when(tagDaoMock.findById(1L)).thenReturn(Optional.of(tag2));
        Mockito.when(daoMock.save(Mockito.any())).thenReturn(certificate);


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
        Mockito.verify(daoMock, Mockito.times(1))
                .findByNameAndDescriptionAndPriceAndDurationAndActive(Mockito.anyString(), Mockito.anyString(),
                        Mockito.any(BigDecimal.class), Mockito.anyInt(), Mockito.eq(true));
        Mockito.verify(tagDaoMock, Mockito.times(1)).findByName("new_tag");
        Mockito.verify(tagDaoMock, Mockito.times(1)).findByName("existed_tag");
        Mockito.verify(tagDaoMock, Mockito.times(1)).findById(1L);
        Mockito.verify(daoMock, Mockito.times(1)).save(Mockito.any());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createNotValidDataCertificateDtoException() {
        Mockito.when(validatorMock.validateCertificateDtoCreate(Mockito.any())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.create(new CertificateDto()));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateDtoCreate(Mockito.any());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createAlreadyExistException() {
        Mockito.when(validatorMock.validateCertificateDtoCreate(Mockito.any())).thenReturn(true);
        Mockito.when(daoMock.findByNameAndDescriptionAndPriceAndDurationAndActive(Mockito.anyString(), Mockito.anyString(),
                        Mockito.any(BigDecimal.class), Mockito.anyInt(), Mockito.eq(true)))
                .thenReturn(Optional.of(new GiftCertificate()));

        CertificateDto dto = new CertificateDto();
        dto.setName("new certificate");
        dto.setDescription("super gift");
        dto.setPrice(new BigDecimal(500));
        dto.setDuration(30);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.create(dto));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_ALREADY_EXIST;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateDtoCreate(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(1))
                .findByNameAndDescriptionAndPriceAndDurationAndActive(Mockito.anyString(), Mockito.anyString(),
                        Mockito.any(BigDecimal.class), Mockito.anyInt(), Mockito.eq(true));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createNotFoundException() {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        Mockito.when(validatorMock.validateCertificateDtoCreate(Mockito.any())).thenReturn(true);
        Mockito.when(daoMock.findByNameAndDescriptionAndPriceAndDurationAndActive(Mockito.anyString(), Mockito.anyString(),
                        Mockito.any(BigDecimal.class), Mockito.anyInt(), Mockito.eq(true)))
                .thenReturn(Optional.empty());
        Mockito.when(tagDaoMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        CertificateDto dto = new CertificateDto();
        dto.setName("new certificate");
        dto.setDescription("super gift");
        dto.setPrice(new BigDecimal(500));
        dto.setDuration(30);
        dto.setTags(Set.of(new TagDto(1L, "tag")));

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.create(dto));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateCertificateDtoCreate(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(1))
                .findByNameAndDescriptionAndPriceAndDurationAndActive(Mockito.anyString(), Mockito.anyString(),
                        Mockito.any(BigDecimal.class), Mockito.anyInt(), Mockito.eq(true));
        Mockito.verify(tagDaoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void update() throws CustomException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(validatorMock.validateCertificateDtoUpdate(Mockito.any())).thenReturn(true);
        Mockito.when(daoMock.findByIdAndActive(Mockito.anyLong(), Mockito.eq(true)))
                .thenReturn(Optional.of(certificate));
        Mockito.when(daoMock.save(Mockito.any(GiftCertificate.class))).thenReturn(certificate);

        CertificateDto expected = DtoEntityConvector.convert(certificate);
        CertificateDto actual = service.update(1L, new CertificateDto());

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(validatorMock, Mockito.times(1))
                .validateCertificateDtoUpdate(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(1))
                .findByIdAndActive(Mockito.anyLong(), Mockito.eq(true));
        Mockito.verify(daoMock, Mockito.times(1)).save(Mockito.any(GiftCertificate.class));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void updateNotFoundException() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(validatorMock.validateCertificateDtoUpdate(Mockito.any())).thenReturn(true);
        Mockito.when(daoMock.findByIdAndActive(Mockito.anyLong(), Mockito.eq(true)))
                .thenReturn(Optional.empty());

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.update(999L, new CertificateDto()));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(validatorMock, Mockito.times(1))
                .validateCertificateDtoUpdate(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(1))
                .findByIdAndActive(Mockito.anyLong(), Mockito.eq(true));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void updateNotValidException1() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.update(-1L, new CertificateDto()));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void updateNotValidException2() {
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(validatorMock.validateCertificateDtoUpdate(Mockito.any())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.update(1L, new CertificateDto()));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(validatorMock, Mockito.times(1))
                .validateCertificateDtoUpdate(Mockito.any());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllByParameters() throws CustomException {
        GiftCertificate certificate1 = new GiftCertificate();
        certificate1.setId(1L);
        GiftCertificate certificate2 = new GiftCertificate();
        certificate2.setId(2L);
        Page<GiftCertificate> certificates = new PageImpl<>(List.of(certificate1, certificate2));
        Mockito.when(validatorMock.validatePageSize(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
        Mockito.when(validatorMock.validateSearchParameters(Mockito.anyMap())).thenReturn(true);
        Mockito.when(daoMock.findAll(Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(certificates);

        Map<SearchParameterName, String> param = new HashMap<>();
        param.put(SearchParameterName.TAG, "tag_1");
        param.put(SearchParameterName.ACTIVE, "true");
        param.put(SearchParameterName.SORT_BY, "date.asc");
        List<CertificateDto> expected = DtoEntityConvector.convertCertificates(certificates.toList());
        List<CertificateDto> actual = service.findAllByParameters(param, 1, 10);

        Mockito.verify(validatorMock, Mockito.times(1))
                .validatePageSize(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(validatorMock, Mockito.times(1))
                .validateSearchParameters(Mockito.anyMap());
        Mockito.verify(daoMock, Mockito.times(1))
                .findAll(Mockito.any(), Mockito.any(Pageable.class));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllByParametersNotValidException1() {
        Mockito.when(validatorMock.validatePageSize(Mockito.anyInt(), Mockito.anyInt())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findAllByParameters(new HashMap<>(), -1, -5));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1))
                .validatePageSize(Mockito.anyInt(), Mockito.anyInt());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllByParametersNotValidException2() {
        Mockito.when(validatorMock.validatePageSize(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
        Mockito.when(validatorMock.validateSearchParameters(Mockito.anyMap())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findAllByParameters(new HashMap<>(), 1, 5));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1))
                .validatePageSize(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(validatorMock, Mockito.times(1))
                .validateSearchParameters(Mockito.anyMap());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllByTags() throws CustomException {
        GiftCertificate certificate1 = new GiftCertificate();
        certificate1.setId(1L);
        GiftCertificate certificate2 = new GiftCertificate();
        certificate2.setId(2L);
        List<GiftCertificate> certificates = List.of(certificate1, certificate2);
        Mockito.when(validatorMock.validatePageSize(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
        Mockito.when(validatorMock.validateTagDto(Mockito.any())).thenReturn(true);
        Mockito.when(daoMock.findAllByTagsNamesAndActive(Mockito.any(), Mockito.eq(true),
                Mockito.any(Pageable.class))).thenReturn(certificates);

        List<CertificateDto> expected = DtoEntityConvector.convertCertificates(certificates);
        List<CertificateDto> actual = service.findAllByTags(new String[]{"tag_1", "tag_2"}, 1, 5);

        Mockito.verify(validatorMock, Mockito.times(1))
                .validatePageSize(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(validatorMock, Mockito.times(2)).validateTagDto(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(1)).findAllByTagsNamesAndActive(Mockito.any(),
                Mockito.eq(true), Mockito.any(Pageable.class));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findByTagsNotValidException1() {
        Mockito.when(validatorMock.validatePageSize(Mockito.anyInt(), Mockito.anyInt())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findAllByTags(new String[]{"tag"}, -1, -5));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1))
                .validatePageSize(Mockito.anyInt(), Mockito.anyInt());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findByTagsNotValidException2() {
        Mockito.when(validatorMock.validatePageSize(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
        Mockito.when(validatorMock.validateTagDto(Mockito.any())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findAllByTags(new String[]{"wrong tag 1"}, 1, 5));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1))
                .validatePageSize(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(validatorMock, Mockito.times(1)).validateTagDto(Mockito.any());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllLastPage() throws CustomException {
        Mockito.when(daoMock.countByActive(Mockito.eq(true))).thenReturn(101L);

        int expected = 11;
        int actual = service.findAllLastPage(10);

        Mockito.verify(daoMock, Mockito.times(1)).countByActive(Mockito.eq(true));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllLastPageNotValidException() {
        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findAllLastPage(-10));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllByParametersLastPage() throws CustomException {
        Mockito.when(validatorMock.validateSearchParameters(Mockito.anyMap())).thenReturn(true);
        Mockito.when(daoMock.count(Mockito.any())).thenReturn(101L);

        Map<SearchParameterName, String> param = new HashMap<>();
        param.put(SearchParameterName.TAG, "tag_1");
        param.put(SearchParameterName.ACTIVE, "true");
        param.put(SearchParameterName.SORT_BY, "date.asc");
        int expected = 11;
        int actual = service.findAllByParametersLastPage(param, 10);

        Mockito.verify(validatorMock, Mockito.times(1))
                .validateSearchParameters(Mockito.anyMap());
        Mockito.verify(daoMock, Mockito.times(1)).count(Mockito.any());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllByParametersLastPageNotValidException1() {
        Mockito.when(validatorMock.validateSearchParameters(Mockito.anyMap())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findAllByParametersLastPage(new HashMap<>(), 5));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1))
                .validateSearchParameters(Mockito.anyMap());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllByParametersLastPageNotValidException2() {
        Mockito.when(validatorMock.validateSearchParameters(Mockito.anyMap())).thenReturn(true);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findAllByParametersLastPage(new HashMap<>(), -5));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1))
                .validateSearchParameters(Mockito.anyMap());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllByTagsLastPage() throws CustomException {
        Mockito.when(validatorMock.validateTagDto(Mockito.any())).thenReturn(true);
        Mockito.when(daoMock.countByTagsNamesAndActive(Mockito.any(), Mockito.eq(true))).thenReturn(101);

        int expected = 11;
        int actual = service.findAllByTagsLastPage(new String[]{"tag_1", "tag_2"}, 10);

        Mockito.verify(validatorMock, Mockito.times(2)).validateTagDto(Mockito.any());
        Mockito.verify(daoMock, Mockito.times(1))
                .countByTagsNamesAndActive(Mockito.any(), Mockito.eq(true));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllByTagsLastPageNotValidException1() {
        Mockito.when(validatorMock.validateTagDto(Mockito.any())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findAllByTagsLastPage(new String[]{"wrong tag 1"},  10));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateTagDto(Mockito.any());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllByTagsLastPageNotValidException2() {
        Mockito.when(validatorMock.validateTagDto(Mockito.any())).thenReturn(true);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> service.findAllByTagsLastPage(new String[]{"tag"},  -10));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1)).validateTagDto(Mockito.any());
        Assertions.assertEquals(expected, actual);
    }
}