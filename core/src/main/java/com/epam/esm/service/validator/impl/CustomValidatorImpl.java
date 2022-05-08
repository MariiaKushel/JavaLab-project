package com.epam.esm.service.validator.impl;

import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.service.SortingType;
import com.epam.esm.service.dto.GiftCertificateDto;
import com.epam.esm.service.validator.CustomValidator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.epam.esm.service.SearchParameterName.*;

@Component
public class CustomValidatorImpl implements CustomValidator {

    private static final long MAX_ID = (long) Math.pow(2, 63);
    private static final String TAG_NAME_REGEX = "\\w+";
    private static final String GIFT_CERTIFICATE_NAME_REGEX = "[\\w\\s]+";
    private static final String DESCRIPTION_REGEX = "[\\p{Graph}\\s]+";
    private static final BigDecimal MAX_PRICE = new BigDecimal("999999.99");
    private static final int MAX_DURATION = 180;

    @Override
    public boolean validateEntityId(long id) {
        return id > 0 && id < MAX_ID;
    }

    @Override
    public boolean validateCustomTag(CustomTag tag) {
        String name = tag.getName();
        return name != null && name.matches(TAG_NAME_REGEX);
    }

    @Override
    public boolean validateGiftCertificateDtoCreate(GiftCertificateDto giftCertificateDto) {
        String name = giftCertificateDto.getName();
        if (name == null || !name.matches(GIFT_CERTIFICATE_NAME_REGEX)) {
            return false;
        }
        String description = giftCertificateDto.getDescription();
        if (description == null || !description.matches(DESCRIPTION_REGEX)) {
            return false;
        }
        BigDecimal price = giftCertificateDto.getPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0 || price.compareTo(MAX_PRICE) > 0) {
            return false;
        }
        int duration = giftCertificateDto.getDuration();
        if (duration <= 0 || duration > MAX_DURATION) {
            return false;
        }
        List<CustomTag> tags = giftCertificateDto.getTags();
        if (tags == null) {
            return true;
        }
        boolean isValid = tags.stream()
                .filter(t -> !validateCustomTag(t))
                .findAny()
                .isEmpty();
        return isValid;
    }

    @Override
    public boolean validateGiftCertificateDtoUpdate(GiftCertificateDto giftCertificateDto) {
        String name = giftCertificateDto.getName();
        if (name != null && !name.matches(GIFT_CERTIFICATE_NAME_REGEX)) {
            return false;
        }
        String description = giftCertificateDto.getDescription();
        if (description != null && !description.matches(DESCRIPTION_REGEX)) {
            return false;
        }
        BigDecimal price = giftCertificateDto.getPrice();
        if (price != null && (price.compareTo(BigDecimal.ZERO) <= 0 || price.compareTo(MAX_PRICE) > 0)) {
            return false;
        }
        int duration = giftCertificateDto.getDuration();
        if (duration < 0 || duration > MAX_DURATION) {
            return false;
        }
        List<CustomTag> tags = giftCertificateDto.getTags();
        if (tags == null) {
            return true;
        }
        boolean isValid = tags.stream()
                .filter(t -> !validateCustomTag(t))
                .findAny()
                .isEmpty();
        return isValid;
    }

    @Override
    public boolean validateSearchParameters(Map<String, String> parameters) {
        String tag = parameters.get(TAG);
        if (tag != null && !tag.matches(TAG_NAME_REGEX)) {
            return false;
        }
        String name = parameters.get(NAME);
        if (name != null && !name.matches(GIFT_CERTIFICATE_NAME_REGEX)) {
            return false;
        }
        String description = parameters.get(DESCRIPTION);
        if (description != null && !description.matches(DESCRIPTION_REGEX)) {
            return false;
        }
        String sorting = parameters.get(SORTING);
        return sorting != null
                ? Stream.of(SortingType.values())
                        .anyMatch(e -> sorting.toUpperCase().equals(e.toString()))
                : true;
    }

}
