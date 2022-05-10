package com.epam.esm.service.impl;

import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.validator.CustomValidator;
import com.epam.esm.service.validator.impl.CustomValidatorImpl;
import com.epam.esm.util.DtoEntityConvector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

class TagServiceImplTest {

    private CustomTagDao daoMock;
    private CustomValidator validator;
    private TagService service;

    public TagServiceImplTest() {
        this.daoMock = Mockito.mock(CustomTagDao.class);
        this.validator = new CustomValidatorImpl();
        this.service = new TagServiceImpl(daoMock, validator);
    }

    @Test
    void findById() throws CustomException {
        CustomTag tag = new CustomTag(1L, "tag");
        Mockito.when(daoMock.findById(1L)).thenReturn(Optional.of(tag));
        TagDto expected = DtoEntityConvector.convert(tag);
        TagDto actual = service.findById(1L);

        Mockito.verify(daoMock, Mockito.times(1)).findById(1L);
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findByIdNotFoundException() {
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.findById(999L));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(daoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findByIdNotValidException() {
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.findById(-1L));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findAll() throws CustomException {
        List<CustomTag> tags = List.of(new CustomTag(1L, "tag_1"), new CustomTag(2L, "tag_2"));
        Mockito.when(daoMock.findAll(Mockito.anyInt(), Mockito.anyInt())).thenReturn(tags);
        List<TagDto> expected = DtoEntityConvector.convertTags(tags);
        List<TagDto> actual = service.findAll(1, 5);

        Mockito.verify(daoMock, Mockito.times(1)).findAll(Mockito.anyInt(), Mockito.anyInt());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void delete() throws CustomException {
        CustomTag tag = new CustomTag(1L, "tag");
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));
        Mockito.doNothing().when(daoMock).delete(Mockito.any());
        service.delete(1L);

        Mockito.verify(daoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1)).delete(Mockito.any());
    }

    @Test
    void deleteNotFoundException() {
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.delete(999L));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_NOT_FOUND;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(daoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void deleteNotValidException() {
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.delete(-1L));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Assertions.assertEquals(actual, expected);
    }

    @Test
    void create() throws CustomException {
        CustomTag tag = new CustomTag(1L, "tag");
        CustomTag tagBlank = new CustomTag("tag");
        Mockito.when(daoMock.save(tagBlank)).thenReturn(tag);
        TagDto expected = DtoEntityConvector.convert(tag);
        TagDto actual = service.create(new TagDto("tag"));

        Mockito.verify(daoMock, Mockito.times(1)).save(tagBlank);
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void createAlreadyExistException() {
        CustomTag tag = new CustomTag(1L, "tag");
        Mockito.when(daoMock.findByName("tag")).thenReturn(Optional.of(tag));
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.create(new TagDto("tag")));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_ALREADY_EXIST;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(daoMock, Mockito.times(1)).findByName("tag");
        Assertions.assertEquals(actual, expected);
    }

    public static Object[][] createNotValidExceptionDataProvider() {
        return new Object[][]{
                {new TagDto("tag!!!")},
                {new TagDto(1L, "tag")}
        };
    }

    @ParameterizedTest
    @MethodSource("createNotValidExceptionDataProvider")
    void createNotValidException(TagDto dto) {
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.create(dto));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findTheMostWidelyTag() {
        CustomTag tag = new CustomTag(1L, "tag");
        Mockito.when(daoMock.findTheMostWidelyTag()).thenReturn(tag);
        TagDto expected = DtoEntityConvector.convert(tag);
        TagDto actual = service.findTheMostWidelyTag();

        Mockito.verify(daoMock, Mockito.times(1)).findTheMostWidelyTag();
        Assertions.assertEquals(actual, expected);
    }
}