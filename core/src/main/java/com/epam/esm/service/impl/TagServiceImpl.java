package com.epam.esm.service.impl;

import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.validator.CustomValidator;
import com.epam.esm.util.DtoEntityConvector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.epam.esm.exception.CustomErrorCode.RESOURCE_NOT_FOUND;

@Service
public class TagServiceImpl implements TagService {

    private CustomTagDao dao;
    private CustomValidator validator;

    @Autowired
    public TagServiceImpl(CustomTagDao dao, CustomValidator validator) {
        this.dao = dao;
        this.validator = validator;
    }

    @Override
    public TagDto findById(long id) throws CustomException {
        boolean isValid = validator.validateEntityId(id);
        if (!isValid) {
            throw new CustomException("id=" + id, CustomErrorCode.NOT_VALID_DATA);
        }
        Optional<CustomTag> tagOptional = dao.findById(id);
        CustomTag tag = tagOptional
                .orElseThrow(() -> new CustomException("id=" + id, RESOURCE_NOT_FOUND));
        return DtoEntityConvector.convert(tag);
    }

    @Override
    public List<TagDto> findAll(int page, int size) {
        List<CustomTag> tags = dao.findAll(page, size);
        return DtoEntityConvector.convertTags(tags);
    }

    @Override
    public void delete(long id) throws CustomException {
        boolean isValid = validator.validateEntityId(id);
        if (!isValid) {
            throw new CustomException("id=" + id, CustomErrorCode.NOT_VALID_DATA);
        }
        Optional<CustomTag> tagOptional = dao.findById(id);
        CustomTag tag = tagOptional
                .orElseThrow(() -> new CustomException("id=" + id, RESOURCE_NOT_FOUND));
        dao.delete(tag);
    }

    @Override
    public TagDto create(TagDto dto) throws CustomException {
        boolean isValid = validator.validateTagDto(dto);
        if (!isValid || dto.getId() != null) {
            throw new CustomException("name=" + dto.getName(), CustomErrorCode.NOT_VALID_DATA);
        }
        Optional<CustomTag> tagOptional = dao.findByName(dto.getName());
        if (tagOptional.isPresent()) {
            throw new CustomException("name=" + dto.getName(),
                    CustomErrorCode.RESOURCE_ALREADY_EXIST);
        }
        CustomTag tag = dao.save(DtoEntityConvector.convert(dto));
        return DtoEntityConvector.convert(tag);
    }

    @Override
    public TagDto findTheMostWidelyTags() {
        CustomTag tag = dao.findTheMostWidelyTags();
        return DtoEntityConvector.convert(tag);
    }

    @Override
    public long count() {
        return dao.count();
    }
}
