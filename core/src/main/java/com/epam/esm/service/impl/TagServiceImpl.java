package com.epam.esm.service.impl;

import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.validator.CustomValidator;
import com.epam.esm.util.DtoEntityConvector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.epam.esm.exception.CustomErrorCode.NOT_VALID_DATA;
import static com.epam.esm.exception.CustomErrorCode.RESOURCE_ALREADY_EXIST;
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
            throw new CustomException("id=" + id, NOT_VALID_DATA);
        }
        Optional<CustomTag> tagOptional = dao.findById(id);
        CustomTag tag = tagOptional
                .orElseThrow(() -> new CustomException("id=" + id, RESOURCE_NOT_FOUND));
        return DtoEntityConvector.convert(tag);
    }

    @Override
    public List<TagDto> findAll(int page, int size) throws CustomException {
        boolean isValidPageSize = validator.validatePageSize(page, size);
        if (!isValidPageSize) {
            throw new CustomException("page=" + page + "; size=" + size, NOT_VALID_DATA);
        }
        Pageable paging = PageRequest.of(page - 1, size);
        List<CustomTag> tags = dao.findAll(paging).toList();
        return DtoEntityConvector.convertTags(tags);
    }

    @Override
    public void delete(long id) throws CustomException {
        boolean isValid = validator.validateEntityId(id);
        if (!isValid) {
            throw new CustomException("id=" + id, NOT_VALID_DATA);
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
            throw new CustomException("name=" + dto.getName(), NOT_VALID_DATA);
        }
        Optional<CustomTag> tagOptional = dao.findByName(dto.getName());
        if (tagOptional.isPresent()) {
            throw new CustomException("name=" + dto.getName(),
                    RESOURCE_ALREADY_EXIST);
        }
        CustomTag tag = dao.save(DtoEntityConvector.convert(dto));
        return DtoEntityConvector.convert(tag);
    }

    @Override
    public TagDto findTheMostWidelyTag() {
        CustomTag tag = dao.findTheMostWidelyTag();
        return DtoEntityConvector.convert(tag);
    }

    @Override
    public int findAllLastPage(int size) throws CustomException {
        if (size < 1) {
            throw new CustomException("size=" + size, NOT_VALID_DATA);
        }
        int quantity = (int) dao.count();
        return quantity % size == 0
                ? quantity / size
                : quantity / size + 1;
    }
}
