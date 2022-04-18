package com.epam.esm.service.impl;

import com.epam.esm.dao.ColumnName;
import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.dto.GiftCertificateDto;
import com.epam.esm.util.DtoEntityConvector;
import com.epam.esm.service.validator.CustomValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {

    private GiftCertificateDao giftCertificateDao;
    private CustomTagDao customTagDao;
    private CustomValidator validator;

    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateDao giftCertificateDao,
                                      CustomTagDao customTagDao,
                                      CustomValidator validator) {
        this.giftCertificateDao = giftCertificateDao;
        this.customTagDao = customTagDao;
        this.validator = validator;
    }

    @Override
    public Optional<GiftCertificateDto> findById(long id) throws CustomException {
        Optional<GiftCertificateDto> dtoOptional = Optional.empty();
        boolean isValid = validator.validateEntityId(id);
        if (!isValid) {
            throw new CustomException("Not valid data (id=" + id + ")", CustomErrorCode.NOT_VALID_DATA);
        }
        Optional<GiftCertificate> giftCertificate = giftCertificateDao.findById(id);
        if (giftCertificate.isPresent()) {
            GiftCertificateDto dto = constructDto(giftCertificate.get());
            dtoOptional = Optional.of(dto);
        }
        return dtoOptional;
    }

    @Override
    public List<GiftCertificateDto> findAll() {
        List<GiftCertificate> giftCertificateList = giftCertificateDao.findAll();
        List<GiftCertificateDto> dtoList = constructDtoList(giftCertificateList);
        return dtoList;
    }

    @Override
    public void delete(long id) throws CustomException {
        boolean isValid = validator.validateEntityId(id);
        if (!isValid) {
            throw new CustomException("Not valid data (id=" + id + ")", CustomErrorCode.NOT_VALID_DATA);
        }
        giftCertificateDao.delete(id);
    }

    @Override
    @Transactional
    public GiftCertificateDto create(GiftCertificateDto giftCertificateDto) throws CustomException {
        boolean isValid = validator.validateGiftCertificateDtoCreate(giftCertificateDto);
        if (!isValid) {
            throw new CustomException("Not valid data (" + giftCertificateDto + ")", CustomErrorCode.NOT_VALID_DATA);
        }

        GiftCertificate giftCertificate = DtoEntityConvector.convert(giftCertificateDto);
        GiftCertificate newGiftCertificate = giftCertificateDao.save(giftCertificate);
        long newGiftCertificateId = newGiftCertificate.getId();

        List<CustomTag> tags = giftCertificateDto.getTags();
        refreshTagList(newGiftCertificateId, tags);

        GiftCertificateDto newDto = constructDto(newGiftCertificate);
        return newDto;
    }

    @Override
    @Transactional
    public GiftCertificateDto update(long id, GiftCertificateDto giftCertificateDto) throws CustomException {
        boolean isValid = validator.validateGiftCertificateDtoUpdate(giftCertificateDto);
        if (!isValid) {
            throw new CustomException("Not valid data (" + giftCertificateDto + ")", CustomErrorCode.NOT_VALID_DATA);
        }
        if (giftCertificateDao.findById(id).isEmpty()) {
            throw new CustomException("Requested resource not found (id=" + id +")", CustomErrorCode.RESOURCE_NOT_FOUND);
        }

        List<CustomTag> tags = giftCertificateDto.getTags();
        refreshTagList(id, tags);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(ColumnName.ID_GIFT_CERTIFICATE, String.valueOf(id));
        parameters.put(ColumnName.NAME_GIFT_CERTIFICATE, giftCertificateDto.getName());
        parameters.put(ColumnName.DESCRIPTION, giftCertificateDto.getDescription());
        parameters.put(ColumnName.PRICE, String.valueOf(giftCertificateDto.getPrice()));
        parameters.put(ColumnName.DURATION, String.valueOf(giftCertificateDto.getPrice()));

        GiftCertificate updatedGiftCertificate = giftCertificateDao.update(parameters);
        GiftCertificateDto updatedDto = constructDto(updatedGiftCertificate);
        return updatedDto;
    }

    @Override
    public List<GiftCertificateDto> findAllByParameters(Map<String, String> parameters) throws CustomException {
        List<GiftCertificateDto> dtoList = new ArrayList<>();
        boolean isValid = validator.validateSearchParameters(parameters);
        if (!isValid) {
            throw new CustomException("Not valid data (" + parameters + ")", CustomErrorCode.NOT_VALID_DATA);
        }
        List<GiftCertificate> giftCertificateList = giftCertificateDao.findAllByParameters(parameters);
        dtoList = constructDtoList(giftCertificateList);
        return dtoList;
    }

    /**
     * Method finds all CustomTags by GiftCertificate id
     * and construct GiftCertificateDto based GiftCertificate and list of CustomTags
     * @param giftCertificate
     * @return
     */
    private GiftCertificateDto constructDto(GiftCertificate giftCertificate) {
        long id = giftCertificate.getId();
        List<CustomTag> tag = customTagDao.findAllByGiftCertificateId(id);
        GiftCertificateDto dto = DtoEntityConvector.convert(giftCertificate, tag);
        return dto;
    }

    /**
     * Method finds all CustomTags by each GiftCertificate in the list
     * and construct list of GiftCertificateDto based those data
     * @param giftCertificateList - list of GiftCertificate
     * @return list of GiftCertificateDto
     */
    private List<GiftCertificateDto> constructDtoList(List<GiftCertificate> giftCertificateList) {
        List<GiftCertificateDto> dtoList = giftCertificateList.stream()
                .map(e -> constructDto(e))
                .toList();
        return dtoList;
    }

    /**
     * Method create coupling between CustomTag and GiftCertificate.
     * If such CustomTag does not exist, then it creates new CustomTag
     * @param giftCertificateId GiftCertificate id
     * @param tags list of CustomTags
     */
    private void refreshTagList(long giftCertificateId, List<CustomTag> tags) {
        for (CustomTag tag : tags) {
            String name = tag.getName();
            Optional<CustomTag> tagOptional = customTagDao.findByName(name);
            if (tagOptional.isEmpty()) {
                CustomTag newCustomTag = customTagDao.save(tag);
                long newTagId = newCustomTag.getId();
                giftCertificateDao.saveCoupling(giftCertificateId, newTagId);
            } else {
                long oldTagId = tagOptional.get().getId();
                giftCertificateDao.saveCoupling(giftCertificateId, oldTagId);
            }
        }
    }
}
