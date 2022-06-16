package com.epam.esm.service.impl;

import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.validator.CustomValidator;
import com.epam.esm.util.DtoEntityConvector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

class TagServiceImplTest {

    private CustomTagDao daoMock;
    private CustomValidator validatorMock;
    private TagService service;

    public TagServiceImplTest() {
        this.daoMock = Mockito.mock(CustomTagDao.class);
        this.validatorMock = Mockito.mock(CustomValidator.class);
        this.service = new TagServiceImpl(daoMock, validatorMock);
    }

    @Test
    void findById() throws CustomException {
        CustomTag tag = new CustomTag(1L, "tag");
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));

        TagDto expected = DtoEntityConvector.convert(tag);
        TagDto actual = service.findById(1L);

        Mockito.verify(validatorMock, Mockito.times(1)).validateEntityId(Mockito.anyLong());
        Mockito.verify(daoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertEquals(expected, actual);
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
        List<CustomTag> tagList = List.of(new CustomTag(1L, "tag_1"), new CustomTag(2L, "tag_2"));
        Page<CustomTag> tags = new PageImpl<>(tagList);
        Mockito.when(validatorMock.validatePageSize(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
        Mockito.when(daoMock.findAll((Pageable) Mockito.any())).thenReturn(tags);

        List<TagDto> expected = DtoEntityConvector.convertTags(tags.toList());
        List<TagDto> actual = service.findAll(1, 5);

        Mockito.verify(validatorMock, Mockito.times(1))
                .validatePageSize(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(daoMock, Mockito.times(1)).findAll((Pageable) Mockito.any());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllNotValidException() {
        Mockito.when(validatorMock.validatePageSize(Mockito.anyInt(), Mockito.anyInt())).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.findAll(-1, -5));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1))
                .validatePageSize(Mockito.anyInt(), Mockito.anyInt());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void delete() throws CustomException {
        CustomTag tag = new CustomTag(1L, "tag");
        Mockito.when(validatorMock.validateEntityId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));
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
        CustomTag tag = new CustomTag(1L, "tag");
        Mockito.when(validatorMock.validateTagDto(Mockito.any(TagDto.class))).thenReturn(true);
        Mockito.when(daoMock.findByName(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(daoMock.save(Mockito.any(CustomTag.class))).thenReturn(tag);

        TagDto expected = DtoEntityConvector.convert(tag);
        TagDto actual = service.create(new TagDto("tag"));

        Mockito.verify(validatorMock, Mockito.times(1))
                .validateTagDto(Mockito.any(TagDto.class));
        Mockito.verify(daoMock, Mockito.times(1)).findByName(Mockito.anyString());
        Mockito.verify(daoMock, Mockito.times(1)).save(Mockito.any(CustomTag.class));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createAlreadyExistException() {
        CustomTag tag = new CustomTag(1L, "tag");
        Mockito.when(validatorMock.validateTagDto(Mockito.any(TagDto.class))).thenReturn(true);
        Mockito.when(daoMock.findByName(Mockito.anyString())).thenReturn(Optional.of(tag));
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.create(new TagDto("tag")));
        CustomErrorCode expected = CustomErrorCode.RESOURCE_ALREADY_EXIST;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1))
                .validateTagDto(Mockito.any(TagDto.class));
        Mockito.verify(daoMock, Mockito.times(1)).findByName(Mockito.anyString());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createNotValidException() {
        TagDto dto = new TagDto(1L, "wrong tag name");
        Mockito.when(validatorMock.validateTagDto(Mockito.any(TagDto.class))).thenReturn(false);

        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.create(dto));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Mockito.verify(validatorMock, Mockito.times(1))
                .validateTagDto(Mockito.any(TagDto.class));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findTheMostWidelyTag() {
        CustomTag tag = new CustomTag(1L, "tag");
        Mockito.when(daoMock.findTheMostWidelyTag()).thenReturn(tag);

        TagDto expected = DtoEntityConvector.convert(tag);
        TagDto actual = service.findTheMostWidelyTag();

        Mockito.verify(daoMock, Mockito.times(1)).findTheMostWidelyTag();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllLastPage() throws CustomException {
        Mockito.when(daoMock.count()).thenReturn(101L);

        int expected = 11;
        int actual = service.findAllLastPage(10);

        Mockito.verify(daoMock, Mockito.times(1)).count();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllLastPageNotValidException() {
        CustomException e = Assertions.assertThrows(CustomException.class, () -> service.findAllLastPage(-1));
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();

        Assertions.assertEquals(expected, actual);
    }
}