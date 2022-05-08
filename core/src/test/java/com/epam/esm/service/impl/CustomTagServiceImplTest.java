package com.epam.esm.service.impl;

import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.CustomTagService;
import com.epam.esm.service.validator.CustomValidator;
import com.epam.esm.service.validator.impl.CustomValidatorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class CustomTagServiceImplTest {

    private CustomTagDao daoMock;
    private CustomValidator validator;
    private CustomTagService service;

    public CustomTagServiceImplTest() {
        this.daoMock = Mockito.mock(CustomTagDao.class);
        this.validator = new CustomValidatorImpl();
        this.service = new CustomTagServiceImpl(daoMock, validator);
    }

    @Test
    void findById() throws CustomException {
        CustomTag tag = new CustomTag(1l, "new_tag");
        Optional<CustomTag> expected = Optional.of(tag);
        Mockito.when(daoMock.findById(1L)).thenReturn(expected);
        Optional<CustomTag> actual = service.findById(1L);
        Mockito.verify(daoMock, Mockito.times(1)).findById(1L);
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void findByIdEmpty() throws CustomException {
        Mockito.when(daoMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Optional<CustomTag> actual = service.findById(1L);
        Mockito.verify(daoMock, Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertTrue(actual.isEmpty());
    }

    @Test
    void findByIdException() throws CustomException {
        Assertions.assertThrows(CustomException.class, () -> {
            service.findById(-1L);
        });
    }

    @Test
    void findAll() {
        List<CustomTag> expected = new ArrayList<>();
        CustomTag tag1 = new CustomTag(1L, "tag_1");
        CustomTag tag2 = new CustomTag(2L, "tag_2");
        expected.add(tag1);
        expected.add(tag2);
        Mockito.when(daoMock.findAll()).thenReturn(expected);
        List<CustomTag> actual = service.findAll();
        Mockito.verify(daoMock, Mockito.times(1)).findAll();
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void delete() throws CustomException {
        Mockito.doNothing().when(daoMock).delete(Mockito.anyLong());
        service.delete(1L);
        Mockito.verify(daoMock, Mockito.times(1)).delete(Mockito.anyLong());
    }

    @Test
    void deleteException() throws CustomException {
        Assertions.assertThrows(CustomException.class, () -> {
            service.delete(-1L);
        });
    }

    @Test
    void create() throws CustomException {
        CustomTag expected = new CustomTag(1L, "tag");
        CustomTag tag = new CustomTag(0L, "tag");
        Mockito.when(daoMock.save(tag)).thenReturn(expected);
        CustomTag actual = service.create(tag);
        Mockito.verify(daoMock, Mockito.times(1)).save(tag);
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void createExceptionNotValid() throws CustomException {
        CustomTag tag = new CustomTag("tag!!!");
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            service.create(tag);
        });
        CustomErrorCode expected = CustomErrorCode.NOT_VALID_DATA;
        CustomErrorCode actual = e.getCustomErrorCode();
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void createExceptionResourceExist() throws CustomException {
        CustomTag tag = new CustomTag(1L, "tag");
        Mockito.when(daoMock.findByName("tag")).thenReturn(Optional.of(tag));
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            service.create(tag);
        });
        Mockito.verify(daoMock, Mockito.times(1)).findByName("tag");
        CustomErrorCode expected = CustomErrorCode.RESOURCE_ALREADY_EXIST;
        CustomErrorCode actual = e.getCustomErrorCode();
        Assertions.assertEquals(actual, expected);
    }

}