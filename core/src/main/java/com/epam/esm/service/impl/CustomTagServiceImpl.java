package com.epam.esm.service.impl;

import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.CustomTagService;
import com.epam.esm.service.validator.CustomValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomTagServiceImpl implements CustomTagService {

    private CustomTagDao customTagDao;
    private CustomValidator validator;

    @Autowired
    public CustomTagServiceImpl(CustomTagDao customTagDao, CustomValidator validator) {
        this.customTagDao = customTagDao;
        this.validator = validator;
    }

    @Override
    public Optional<CustomTag> findById(long id) throws CustomException {
        boolean isValid = validator.validateEntityId(id);
        if (!isValid) {
            throw new CustomException("Not valid data (id=" + id + ")", CustomErrorCode.NOT_VALID_DATA);
        }
        return customTagDao.findById(id);
    }

    @Override
    public List<CustomTag> findAll() {
        return customTagDao.findAll();
    }

    @Override
    public void delete(long id) throws CustomException {
        boolean isValid = validator.validateEntityId(id);
        if (!isValid) {
            throw new CustomException("Not valid data (id=" + id + ")", CustomErrorCode.NOT_VALID_DATA);
        }
        customTagDao.delete(id);
    }

    @Override
    public CustomTag create(CustomTag tag) throws CustomException {
        boolean isValid = validator.validateCustomTag(tag);
        if (!isValid) {
            throw new CustomException("Not valid data (name=" + tag.getName() + ")", CustomErrorCode.NOT_VALID_DATA);
        }
        if (customTagDao.findByName(tag.getName()).isPresent()) {
            throw new CustomException("Resource already exist (name=" + tag.getName() + ")",
                    CustomErrorCode.RESOURCE_ALREADY_EXIST);
        }
        return customTagDao.save(tag);
    }
}
