package com.epam.esm.service.impl;

import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.SearchParameterName;
import com.epam.esm.service.dto.GiftCertificateDto;
import com.epam.esm.service.validator.CustomValidator;
import com.epam.esm.service.validator.impl.CustomValidatorImpl;
import com.epam.esm.util.DtoEntityConvector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

class GiftCertificateServiceImplTest {

    private GiftCertificateDao giftDaoMock;
    private CustomTagDao tagDaoMock;
    private CustomValidator validator;
    private GiftCertificateService service;

    public GiftCertificateServiceImplTest() {
        this.giftDaoMock = Mockito.mock(GiftCertificateDao.class);
        this.tagDaoMock = Mockito.mock(CustomTagDao.class);
        this.validator = new CustomValidatorImpl();
        this.service = new GiftCertificateServiceImpl(giftDaoMock, tagDaoMock, validator);
    }

    @Test
    void findById() throws CustomException {
        List<CustomTag> tags = new ArrayList<>();
        GiftCertificate giftCertificate = GiftCertificate.newBuilder().setEntityId(1L).build();
        Mockito.when(tagDaoMock.findAllByGiftCertificateId(Mockito.anyLong())).thenReturn(tags);
        Mockito.when(giftDaoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(giftCertificate));

        GiftCertificateDto expected = DtoEntityConvector.convert(giftCertificate, tags);
        GiftCertificateDto actual = service.findById(1L).get();

        Mockito.verify(tagDaoMock, Mockito.times(1)).findAllByGiftCertificateId(Mockito.anyLong());
        Mockito.verify(giftDaoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findByIdException() {
        Assertions.assertThrows(CustomException.class, () -> {
            service.findById(-1l);
        });
    }

    @Test
    void findAll() {
        List<CustomTag> tags = new ArrayList<>();
        GiftCertificate gift1 = GiftCertificate.newBuilder().setEntityId(1L).build();
        GiftCertificate gift2 = GiftCertificate.newBuilder().setEntityId(2L).build();
        List<GiftCertificate> gifts = new ArrayList<>();
        gifts.add(gift1);
        gifts.add(gift2);
        Mockito.when(tagDaoMock.findAllByGiftCertificateId(Mockito.anyLong())).thenReturn(tags);
        Mockito.when(giftDaoMock.findAll()).thenReturn(gifts);

        GiftCertificateDto dto1 = DtoEntityConvector.convert(gift1, tags);
        GiftCertificateDto dto2 = DtoEntityConvector.convert(gift2, tags);
        List<GiftCertificateDto> expected = new ArrayList<>();
        expected.add(dto1);
        expected.add(dto2);
        List<GiftCertificateDto> actual = service.findAll();
        Mockito.verify(tagDaoMock, Mockito.times(2)).findAllByGiftCertificateId(Mockito.anyLong());
        Mockito.verify(giftDaoMock, Mockito.times(1)).findAll();
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void delete() throws CustomException {
        Mockito.doNothing().when(giftDaoMock).delete(Mockito.anyLong());
        service.delete(1L);
        Mockito.verify(giftDaoMock, Mockito.times(1)).delete(Mockito.anyLong());
    }

    @Test
    void deleteException() throws CustomException {
        Assertions.assertThrows(CustomException.class, () -> {
            service.delete(-1L);
        });
    }

    @Test
    void create() throws CustomException {
        GiftCertificate gift = GiftCertificate.newBuilder()
                .setEntityId(1L)
                .setName("new certificate")
                .setDescription("super gift")
                .setPrice(new BigDecimal(500))
                .setDuration(30)
                .setCreateDate(LocalDateTime.parse("2022-02-01T12:00:00"))
                .setLastUpdateDate(LocalDateTime.parse("2022-02-15T13:00:00"))
                .build();

        Mockito.when(giftDaoMock.save(Mockito.any())).thenReturn(gift);
        Mockito.when(giftDaoMock.saveCoupling(Mockito.anyLong(), Mockito.anyLong())).thenReturn(true);

        Mockito.when(tagDaoMock.findByName("new_tag")).thenReturn(Optional.empty());

        CustomTag oldTag = new CustomTag(1L, "old_tag");
        Mockito.when(tagDaoMock.findByName("old_tag")).thenReturn(Optional.of(oldTag));

        CustomTag newTag = new CustomTag(42L, "new_tag");
        Mockito.when(tagDaoMock.save(Mockito.any())).thenReturn(newTag);

        List<CustomTag> customTagList = new ArrayList<>();
        customTagList.add(newTag);
        customTagList.add(oldTag);
        Mockito.when(tagDaoMock.findAllByGiftCertificateId(Mockito.anyLong())).thenReturn(customTagList);

        GiftCertificateDto dto = new GiftCertificateDto();
        dto.setName("new certificate");
        dto.setDescription("super gift");
        dto.setPrice(new BigDecimal(500));
        dto.setDuration(30);
        dto.setTags(customTagList);

        GiftCertificateDto actual = service.create(dto);
        GiftCertificateDto expected = new GiftCertificateDto();
        expected.setDtoId(1L);
        expected.setName("new certificate");
        expected.setDescription("super gift");
        expected.setPrice(new BigDecimal(500));
        expected.setDuration(30);
        expected.setCreateDate(LocalDateTime.parse("2022-02-01T12:00:00"));
        expected.setLastUpdateDate(LocalDateTime.parse("2022-02-15T13:00:00"));
        expected.setTags(customTagList);

        Mockito.verify(giftDaoMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(giftDaoMock, Mockito.times(2)).saveCoupling(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(tagDaoMock, Mockito.times(1)).findByName("new_tag");
        Mockito.verify(tagDaoMock, Mockito.times(1)).findByName("old_tag");
        Mockito.verify(tagDaoMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(tagDaoMock, Mockito.times(1)).findAllByGiftCertificateId(Mockito.anyLong());

        Assertions.assertEquals(actual, expected);
    }

    @Test
    void createException() {
        Assertions.assertThrows(CustomException.class, () -> {
            GiftCertificateDto dto = new GiftCertificateDto();
            dto.setName("new gift");
            service.create(dto);
        });
    }

    @Test
    void update() throws CustomException {
        GiftCertificate gift = GiftCertificate.newBuilder()
                .setEntityId(1l)
                .setDescription("new description")
                .build();
        Mockito.when(giftDaoMock.update(Mockito.any())).thenReturn(gift);
        Mockito.when(giftDaoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(gift));

        Mockito.when(tagDaoMock.findAllByGiftCertificateId(Mockito.anyLong())).thenReturn(new ArrayList<>());

        GiftCertificateDto dto = new GiftCertificateDto();
        dto.setDescription("new description");
        List<CustomTag> tags = new ArrayList<>();
        dto.setTags(tags);

        GiftCertificateDto actual = service.update(1L, dto);
        GiftCertificateDto expected = new GiftCertificateDto();
        expected.setDtoId(1L);
        expected.setDescription("new description");
        expected.setTags(new ArrayList<>());

        Mockito.verify(giftDaoMock, Mockito.times(1)).update(Mockito.any());
        Mockito.verify(giftDaoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(tagDaoMock, Mockito.times(1)).findAllByGiftCertificateId(Mockito.anyLong());

        Assertions.assertEquals(actual, expected);
    }

    @Test
    void updateExceptionNotValid() {
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            GiftCertificateDto dto = new GiftCertificateDto();
            dto.setPrice(new BigDecimal("-10"));
            service.update(1L, dto);
        });
        Assertions.assertEquals(e.getCustomErrorCode(), CustomErrorCode.NOT_VALID_DATA);
    }

    @Test
    void updateExceptionResourceNotFound() {
        Mockito.when(giftDaoMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            GiftCertificateDto dto = new GiftCertificateDto();
            dto.setDescription("new description");
            service.update(999L, dto);
        });
        Mockito.verify(giftDaoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(e.getCustomErrorCode(), CustomErrorCode.RESOURCE_NOT_FOUND);
    }

    @Test
    void findAllByParameters() throws CustomException {
        List<CustomTag> tags = new ArrayList<>();
        GiftCertificate gift1 = GiftCertificate.newBuilder().setEntityId(1L).build();
        GiftCertificate gift2 = GiftCertificate.newBuilder().setEntityId(2L).build();
        List<GiftCertificate> gifts = new ArrayList<>();
        gifts.add(gift1);
        gifts.add(gift2);
        Mockito.when(tagDaoMock.findAllByGiftCertificateId(Mockito.anyLong())).thenReturn(tags);
        Mockito.when(giftDaoMock.findAllByParameters(Mockito.any())).thenReturn(gifts);

        GiftCertificateDto dto1 = DtoEntityConvector.convert(gift1, tags);
        GiftCertificateDto dto2 = DtoEntityConvector.convert(gift2, tags);
        List<GiftCertificateDto> expected = new ArrayList<>();
        expected.add(dto1);
        expected.add(dto2);
        List<GiftCertificateDto> actual = service.findAllByParameters(new HashMap<>());

        Mockito.verify(tagDaoMock, Mockito.times(2)).findAllByGiftCertificateId(Mockito.anyLong());
        Mockito.verify(giftDaoMock, Mockito.times(1)).findAllByParameters(Mockito.any());

        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findAllByParametersExceptionNotValid() {
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            Map<String, String> parameters = new HashMap<>();
            parameters.put(SearchParameterName.TAG, "12 12");
            service.findAllByParameters(parameters);
        });
    }
}