package com.epam.esm.service.impl;

import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.validator.CustomValidator;
import com.epam.esm.util.DtoEntityConvector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.epam.esm.exception.CustomErrorCode.RESOURCE_NOT_FOUND;

@Service
public class CertificateServiceImpl implements CertificateService {

    private GiftCertificateDao dao;
    private CustomValidator validator;
    private CustomTagDao tagDao;

    @Autowired
    public CertificateServiceImpl(GiftCertificateDao dao,
                                  CustomValidator validator,
                                  CustomTagDao tagDao) {
        this.dao = dao;
        this.validator = validator;
        this.tagDao = tagDao;
    }

    @Override
    public CertificateDto findById(long id) throws CustomException {
        boolean isValid = validator.validateEntityId(id);
        if (!isValid) {
            throw new CustomException("id=" + id, CustomErrorCode.NOT_VALID_DATA);
        }
        Optional<GiftCertificate> certificateOptional = dao.findById(id);
        GiftCertificate certificate = certificateOptional
                .orElseThrow(() -> new CustomException("id=" + id, RESOURCE_NOT_FOUND));
        return DtoEntityConvector.convert(certificate);
    }

    @Override
    public List<CertificateDto> findAll(int page, int size) throws CustomException {
        List<GiftCertificate> certificates = dao.findAll(page, size);
        return DtoEntityConvector.convertCertificates(certificates);
    }

    @Override
    public void delete(long id) throws CustomException {
        boolean isValid = validator.validateEntityId(id);
        if (!isValid) {
            throw new CustomException("id=" + id, CustomErrorCode.NOT_VALID_DATA);
        }
        Optional<GiftCertificate> certificateOptional = dao.findById(id);
        GiftCertificate certificate = certificateOptional
                .orElseThrow(() -> new CustomException("id=" + id, RESOURCE_NOT_FOUND));
        int presentInOrders = certificate.getOrders().size();
        if (presentInOrders > 0) {
            throw new CustomException("certificate id=" + id, CustomErrorCode.LINKED_TO_ANOTHER_RESOURCE);
        }
        dao.delete(certificate);
    }

    @Override
    public CertificateDto create(CertificateDto dto) throws CustomException {
        boolean isValid = validator.validateCertificateDtoCreate(dto);
        if (!isValid || dto.getId() != null) {
            throw new CustomException("name=" + dto.getName()
                    + "; description=" + dto.getDescription()
                    + "; price=" + dto.getPrice()
                    + "; duration=" + dto.getDuration(), CustomErrorCode.NOT_VALID_DATA);
        }
        Optional<GiftCertificate> certificateOptional =
                dao.findNameAndDescriptionAndPriceAndDuration(dto.getName(), dto.getDescription(),
                        dto.getPrice(), dto.getDuration());
        if (certificateOptional.isPresent()) {
            throw new CustomException("name=" + dto.getName()
                    + "; description=" + dto.getDescription()
                    + "; price=" + dto.getPrice()
                    + "; duration=" + dto.getDuration(),
                    CustomErrorCode.RESOURCE_ALREADY_EXIST);
        }

        Set<TagDto> editedTags = prepareTagSet(dto.getTags());
        dto.setTags(editedTags);
        GiftCertificate certificate = DtoEntityConvector.convert(dto);
        GiftCertificate newCertificate = dao.save(certificate);
        return DtoEntityConvector.convert(newCertificate);
    }

    @Override
    public CertificateDto update(long id, CertificateDto dto) throws CustomException {
        boolean isValid = validator.validateCertificateDtoUpdate(dto);
        if (!isValid) {
            throw new CustomException("name=" + dto.getName()
                    + "; description=" + dto.getDescription()
                    + "; price=" + dto.getPrice()
                    + "; duration=" + dto.getDuration()
                    + "; tags=" + dto.getTags()
                    , CustomErrorCode.NOT_VALID_DATA);
        }
        if (dao.findById(id).isEmpty()) {
            throw new CustomException("id=" + id, CustomErrorCode.RESOURCE_NOT_FOUND);
        }
        GiftCertificate certificate = new GiftCertificate();
        String name = dto.getName();
        if (name != null) {
            certificate = dao.updateName(id, name);
        }
        String description = dto.getDescription();
        if (description != null) {
            certificate = dao.updateDescription(id, description);
        }
        BigDecimal price = dto.getPrice();
        if (price != null) {
            certificate = dao.updatePrice(id, price);
        }
        Integer duration = dto.getDuration();
        if (duration != null) {
            certificate = dao.updateDuration(id, duration);
        }
        Set<TagDto> dtoTags = dto.getTags();
        if (dtoTags != null) {
            dtoTags = prepareTagSet(dtoTags);
            Set<CustomTag> tags = DtoEntityConvector.convertDtos(dtoTags);
            certificate = dao.updateTags(id, tags);
        }
        return DtoEntityConvector.convert(certificate);
    }

    @Override
    public CertificateDto updateName(long id, String name) throws CustomException {
        CertificateDto dto = new CertificateDto();
        dto.setName(name);
        boolean isValid = validator.validateCertificateDtoUpdate(dto);
        if (!isValid) {
            throw new CustomException("name=" + name, CustomErrorCode.NOT_VALID_DATA);
        }
        if (dao.findById(id).isEmpty()) {
            throw new CustomException("id=" + id, CustomErrorCode.RESOURCE_NOT_FOUND);
        }
        GiftCertificate certificate = dao.updateName(id, name);
        return DtoEntityConvector.convert(certificate);
    }

    @Override
    public CertificateDto updateDescription(long id, String description) throws CustomException {
        CertificateDto dto = new CertificateDto();
        dto.setDescription(description);
        boolean isValid = validator.validateCertificateDtoUpdate(dto);
        if (!isValid) {
            throw new CustomException("description=" + description, CustomErrorCode.NOT_VALID_DATA);
        }
        if (dao.findById(id).isEmpty()) {
            throw new CustomException("id=" + id, CustomErrorCode.RESOURCE_NOT_FOUND);
        }
        GiftCertificate certificate = dao.updateDescription(id, description);
        return DtoEntityConvector.convert(certificate);
    }

    @Override
    public CertificateDto updatePrice(long id, BigDecimal price) throws CustomException {
        CertificateDto dto = new CertificateDto();
        dto.setPrice(price);
        boolean isValid = validator.validateCertificateDtoUpdate(dto);
        if (!isValid) {
            throw new CustomException("price=" + price, CustomErrorCode.NOT_VALID_DATA);
        }
        if (dao.findById(id).isEmpty()) {
            throw new CustomException("id=" + id, CustomErrorCode.RESOURCE_NOT_FOUND);
        }
        GiftCertificate certificate = dao.updatePrice(id, price);
        return DtoEntityConvector.convert(certificate);
    }

    @Override
    public CertificateDto updateDuration(long id, Integer duration) throws CustomException {
        CertificateDto dto = new CertificateDto();
        dto.setDuration(duration);
        boolean isValid = validator.validateCertificateDtoUpdate(dto);
        if (!isValid) {
            throw new CustomException("duration=" + duration, CustomErrorCode.NOT_VALID_DATA);
        }
        if (dao.findById(id).isEmpty()) {
            throw new CustomException("id=" + id, CustomErrorCode.RESOURCE_NOT_FOUND);
        }
        GiftCertificate certificate = dao.updateDuration(id, duration);
        return DtoEntityConvector.convert(certificate);
    }

    @Override
    public CertificateDto updateTags(long id, Set<TagDto> tagDtos) throws CustomException {
        CertificateDto dto = new CertificateDto();
        dto.setTags(tagDtos);
        boolean isValidName = validator.validateCertificateDtoUpdate(dto);
        if (!isValidName) {
            throw new CustomException("tags=" + tagDtos, CustomErrorCode.NOT_VALID_DATA);
        }
        if (dao.findById(id).isEmpty()) {
            throw new CustomException("id=" + id, CustomErrorCode.RESOURCE_NOT_FOUND);
        }
        tagDtos = prepareTagSet(tagDtos);
        Set<CustomTag> tags = DtoEntityConvector.convertDtos(tagDtos);
        GiftCertificate certificate = dao.updateTags(id, tags);
        return DtoEntityConvector.convert(certificate);
    }

    @Override
    public List<CertificateDto> findAllByParameters(Map<String, String> parameters, int page, int size)
            throws CustomException {
        boolean isValid = validator.validateSearchParameters(parameters);
        if (!isValid) {
            throw new CustomException(parameters.toString(), CustomErrorCode.NOT_VALID_DATA);
        }
        List<GiftCertificate> certificates = dao.findAllByParameters(parameters, page, size);
        return DtoEntityConvector.convertCertificates(certificates);
    }

    @Override
    public List<CertificateDto> findByTags(String[] tags, int page, int size) throws CustomException {
        Set<TagDto> dtos = Stream.of(tags)
                .map(TagDto::new)
                .collect(Collectors.toSet());
        boolean isValidTags = dtos.stream().allMatch(validator::validateTagDto);
        if (!isValidTags) {
            throw new CustomException(Arrays.toString(tags), CustomErrorCode.NOT_VALID_DATA);
        }
        List<GiftCertificate> certificates = dao.findByTags(page, size, tags);
        return DtoEntityConvector.convertCertificates(certificates);
    }

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    public long countByParameters(Map<String, String> parameters) throws CustomException {
        boolean isValid = validator.validateSearchParameters(parameters);
        if (!isValid) {
            throw new CustomException(parameters.toString(), CustomErrorCode.NOT_VALID_DATA);
        }
        return dao.countByParameters(parameters);
    }

    @Override
    public long countByTags(String[] tags) throws CustomException {
        Set<TagDto> dtos = Stream.of(tags)
                .map(TagDto::new)
                .collect(Collectors.toSet());
        boolean isValidTags = dtos.stream().allMatch(validator::validateTagDto);
        if (!isValidTags) {
            throw new CustomException(Arrays.toString(tags), CustomErrorCode.NOT_VALID_DATA);
        }
        return dao.countByTags(tags);
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
                    () -> new CustomException("tag id=" + tagId, CustomErrorCode.RESOURCE_NOT_FOUND));
            if (!currentTag.getName().equals(dto.getName())) {
                throw new CustomException("tag id=" + tagId + "; name=" + currentTag.getName(),
                        CustomErrorCode.RESOURCE_ALREADY_EXIST);
            }
            editedTags.add(dto);
        }
        return editedTags;
    }

}