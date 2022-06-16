package com.epam.esm.service.impl;

import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.dao.entity.GiftCertificate_;
import com.epam.esm.enumeration.SearchParameterName;
import com.epam.esm.enumeration.SortingType;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.validator.CustomValidator;
import com.epam.esm.specification.SpecificationCreator;
import com.epam.esm.util.DtoEntityConvector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.epam.esm.exception.CustomErrorCode.NOT_VALID_DATA;
import static com.epam.esm.exception.CustomErrorCode.RESOURCE_ALREADY_EXIST;
import static com.epam.esm.exception.CustomErrorCode.RESOURCE_NOT_FOUND;

@Service
public class CertificateServiceImpl implements CertificateService {

    private GiftCertificateDao certificateDao;
    private CustomValidator validator;
    private CustomTagDao tagDao;

    @Autowired
    public CertificateServiceImpl(GiftCertificateDao certificateDao,
                                  CustomValidator validator,
                                  CustomTagDao tagDao) {
        this.certificateDao = certificateDao;
        this.validator = validator;
        this.tagDao = tagDao;
    }

    @Override
    public CertificateDto findById(long id) throws CustomException {
        boolean isValid = validator.validateEntityId(id);
        if (!isValid) {
            throw new CustomException("id=" + id, NOT_VALID_DATA);
        }
        Optional<GiftCertificate> certificateOptional = certificateDao.findByIdAndActive(id, true);
        GiftCertificate certificate = certificateOptional
                .orElseThrow(() -> new CustomException("id=" + id, RESOURCE_NOT_FOUND));
        return DtoEntityConvector.convert(certificate);
    }

    @Override
    public List<CertificateDto> findAll(int page, int size) throws CustomException {
        boolean isValidPageSize = validator.validatePageSize(page, size);
        if (!isValidPageSize) {
            throw new CustomException("page=" + page + "; size=" + size, NOT_VALID_DATA);
        }
        Pageable paging = PageRequest.of(page - 1, size);
        List<GiftCertificate> certificates = certificateDao.findAllByActive(true, paging).toList();
        return DtoEntityConvector.convertCertificates(certificates);
    }

    @Override
    public void delete(long id) throws CustomException {
        boolean isValid = validator.validateEntityId(id);
        if (!isValid) {
            throw new CustomException("id=" + id, NOT_VALID_DATA);
        }
        Optional<GiftCertificate> certificateOptional = certificateDao.findByIdAndActive(id, true);
        GiftCertificate certificate = certificateOptional
                .orElseThrow(() -> new CustomException("id=" + id, RESOURCE_NOT_FOUND));
        int presentInOrders = certificate.getOrders().size();
        if (presentInOrders > 0) {
            certificate.setActive(false);
            certificateDao.save(certificate);
        } else {
            certificateDao.delete(certificate);
        }
    }

    @Override
    @Transactional
    public CertificateDto create(CertificateDto dto) throws CustomException {
        boolean isValid = validator.validateCertificateDtoCreate(dto);
        if (!isValid || dto.getId() != null) {
            throw new CustomException("name=" + dto.getName()
                    + "; description=" + dto.getDescription()
                    + "; price=" + dto.getPrice()
                    + "; duration=" + dto.getDuration()
                    + "; tags=" + dto.getTags(),
                    NOT_VALID_DATA);
        }
        Optional<GiftCertificate> certificateOptional =
                certificateDao.findByNameAndDescriptionAndPriceAndDurationAndActive(dto.getName(),
                        dto.getDescription(),
                        dto.getPrice(),
                        dto.getDuration(),
                        true);
        if (certificateOptional.isPresent()) {
            GiftCertificate cert = certificateOptional.get();
            throw new CustomException("name=" + cert.getName()
                    + "; description=" + cert.getDescription()
                    + "; price=" + cert.getPrice()
                    + "; duration=" + cert.getDuration(),
                    RESOURCE_ALREADY_EXIST);
        }
        Set<TagDto> editedTags = prepareTagSet(dto.getTags());
        dto.setTags(editedTags);
        GiftCertificate certificate = DtoEntityConvector.convert(dto);
        certificate.setActive(true);
        GiftCertificate newCertificate = certificateDao.save(certificate);
        return DtoEntityConvector.convert(newCertificate);
    }

    @Override
    public CertificateDto update(long id, CertificateDto dto) throws CustomException {
        boolean isValidId = validator.validateEntityId(id);
        if (!isValidId) {
            throw new CustomException("id=" + id, NOT_VALID_DATA);
        }
        boolean isValidDto = validator.validateCertificateDtoUpdate(dto);
        if (!isValidDto) {
            throw new CustomException("name=" + dto.getName()
                    + "; description=" + dto.getDescription()
                    + "; price=" + dto.getPrice()
                    + "; duration=" + dto.getDuration()
                    + "; tags=" + dto.getTags(),
                    NOT_VALID_DATA);
        }
        GiftCertificate oldCertificate = certificateDao.findByIdAndActive(id, true)
                .orElseThrow(() -> new CustomException("id=" + id, RESOURCE_NOT_FOUND));
        refreshGiftCertificate(dto, oldCertificate);
        GiftCertificate updatedCertificate = certificateDao.save(oldCertificate);
        return DtoEntityConvector.convert(updatedCertificate);
    }

    @Override
    public List<CertificateDto> findAllByParameters(Map<SearchParameterName, String> parameters, int page, int size)
            throws CustomException {
        boolean isValidPageSize = validator.validatePageSize(page, size);
        if (!isValidPageSize) {
            throw new CustomException("page=" + page + "; size=" + size, NOT_VALID_DATA);
        }
        boolean isValid = validator.validateSearchParameters(parameters);
        if (!isValid) {
            throw new CustomException(parameters.toString(), NOT_VALID_DATA);
        }
        Sort sorting = getSorting(parameters.get(SearchParameterName.SORT_BY));
        Pageable paging = PageRequest.of(page - 1, size, sorting);
        Specification<GiftCertificate> specification = SpecificationCreator.getSpecification(parameters);
        List<GiftCertificate> certificates = certificateDao.findAll(specification, paging).toList();
        return DtoEntityConvector.convertCertificates(certificates);
    }

    @Override
    public List<CertificateDto> findAllByTags(String[] tags, int page, int size) throws CustomException {
        boolean isValidPageSize = validator.validatePageSize(page, size);
        if (!isValidPageSize) {
            throw new CustomException("page=" + page + "; size=" + size, NOT_VALID_DATA);
        }
        Set<TagDto> dtos = Stream.of(tags)
                .map(TagDto::new)
                .collect(Collectors.toSet());
        boolean isValidTags = dtos.stream().allMatch(validator::validateTagDto);
        if (!isValidTags || tags.length == 0) {
            throw new CustomException("tags=" + Arrays.toString(tags), NOT_VALID_DATA);
        }
        Pageable paging = PageRequest.of(page - 1, size);
        List<GiftCertificate> certificates = certificateDao.findAllByTagsNamesAndActive(tags, true, paging);
        return DtoEntityConvector.convertCertificates(certificates);
    }

    @Override
    public int findAllLastPage(int size) throws CustomException {
        if (size < 1) {
            throw new CustomException("size=" + size, NOT_VALID_DATA);
        }
        int quantity = (int) certificateDao.countByActive(true);
        int lastPage = quantity % size == 0
                ? quantity / size
                : quantity / size + 1;
        return lastPage;
    }

    @Override
    public int findAllByParametersLastPage(Map<SearchParameterName, String> parameters, int size)
            throws CustomException {
        boolean isValid = validator.validateSearchParameters(parameters);
        if (!isValid) {
            throw new CustomException(parameters.toString(), NOT_VALID_DATA);
        }
        if (size < 1) {
            throw new CustomException("size=" + size, NOT_VALID_DATA);
        }
        Specification<GiftCertificate> specification = SpecificationCreator.getSpecification(parameters);
        int quantity = (int) certificateDao.count(specification);
        int lastPage = quantity % size == 0
                ? quantity / size
                : quantity / size + 1;
        return lastPage;
    }

    @Override
    public int findAllByTagsLastPage(String[] tags, int size) throws CustomException {
        Set<TagDto> dtos = Stream.of(tags)
                .map(TagDto::new)
                .collect(Collectors.toSet());
        boolean isValidTags = dtos.stream().allMatch(validator::validateTagDto);
        if (!isValidTags || tags.length == 0) {
            throw new CustomException("tags=" + Arrays.toString(tags), NOT_VALID_DATA);
        }
        if (size < 1) {
            throw new CustomException("size=" + size, NOT_VALID_DATA);
        }
        int quantity = certificateDao.countByTagsNamesAndActive(tags, true);
        int lastPage = quantity % size == 0
                ? quantity / size
                : quantity / size + 1;
        return lastPage;
    }

    private void refreshGiftCertificate(CertificateDto dto, GiftCertificate certificate) throws CustomException {
        String name = dto.getName();
        if (name != null) {
            certificate.setName(name);
        }
        String description = dto.getDescription();
        if (description != null) {
            certificate.setDescription(description);
        }
        BigDecimal price = dto.getPrice();
        if (price != null) {
            certificate.setPrice(price);
        }
        Integer duration = dto.getDuration();
        if (duration != null) {
            certificate.setDuration(duration);
        }
        Set<TagDto> dtoTags = dto.getTags();
        if (dtoTags != null) {
            dtoTags = prepareTagSet(dtoTags);
            Set<CustomTag> tags = DtoEntityConvector.convertDtos(dtoTags);
            certificate.setTags(tags);
        }
    }

    private Set<TagDto> prepareTagSet(Set<TagDto> dtos) throws CustomException {
        Set<TagDto> editedTags = new HashSet<>();
        for (TagDto dto : dtos) {
            Long tagId = dto.getId();

            if (tagId == null) {
                Optional<CustomTag> tagOptional = tagDao.findByName(dto.getName());
                editedTags.add(tagOptional.isPresent()
                        ? DtoEntityConvector.convert(tagOptional.get())
                        : dto);
                continue;
            }
            Optional<CustomTag> tagOptional = tagDao.findById(tagId);
            CustomTag currentTag = tagOptional.orElseThrow(
                    () -> new CustomException("tag id=" + tagId, RESOURCE_NOT_FOUND));
            editedTags.add(DtoEntityConvector.convert(currentTag));
        }
        return editedTags;
    }

    private Sort getSorting(String sorting) {
        SortingType type = SortingType.getSortingType(sorting);
        Sort sort =
                switch (type) {
                    case NAME_ASC -> Sort.by(Sort.Direction.ASC, GiftCertificate_.NAME);
                    case NAME_DESC -> Sort.by(Sort.Direction.DESC, GiftCertificate_.NAME);
                    case DATE_ASC -> Sort.by(Sort.Direction.ASC, GiftCertificate_.CREATE_DATE);
                    case DATE_DESC -> Sort.by(Sort.Direction.DESC, GiftCertificate_.CREATE_DATE);
                    case DATE_DESC_NAME_ASC -> Sort.by(Sort.Direction.DESC, GiftCertificate_.CREATE_DATE)
                            .and(Sort.by(Sort.Direction.ASC, GiftCertificate_.NAME));
                };
        return sort;
    }
}
