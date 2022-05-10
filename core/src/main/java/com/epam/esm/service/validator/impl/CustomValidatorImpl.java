package com.epam.esm.service.validator.impl;

import com.epam.esm.service.SearchParameterName;
import com.epam.esm.service.SortingType;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.validator.CustomValidator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.epam.esm.service.SearchParameterName.DESCRIPTION;
import static com.epam.esm.service.SearchParameterName.NAME;
import static com.epam.esm.service.SearchParameterName.SORT_BY;
import static com.epam.esm.service.SearchParameterName.TAG;

@Component
public class CustomValidatorImpl implements CustomValidator {

    private static final Long MAX_ID = (long) Math.pow(2, 63);
    private static final BigDecimal MAX_PRICE = new BigDecimal("999999.99");
    private static final int MAX_DURATION = 180;
    private static final String SORTING_TYPE_SPLITTER = ",";

    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("\\w+");
    private static final Pattern CERTIFICATE_NAME_PATTERN = Pattern.compile("[\\w\\s]+");
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("[\\p{Graph}\\s]+");

    private static final Matcher tagNameMatcher = TAG_NAME_PATTERN.matcher("");
    private static final Matcher certificateNameMatcher = CERTIFICATE_NAME_PATTERN.matcher("");
    private static final Matcher descriptionMatcher = DESCRIPTION_PATTERN.matcher("");

    @Override
    public boolean validateEntityId(Long id) {
        return id != null && id > 0 && id < MAX_ID;
    }

    @Override
    public boolean validateTagDto(TagDto dto) {
        String name = dto.getName();
        return name != null && tagNameMatcher.reset(name).matches();
    }

    @Override
    public boolean validateCertificateDtoCreate(CertificateDto dto) {
        String name = dto.getName();
        if (name == null || !certificateNameMatcher.reset(name).matches()) {
            return false;
        }
        String description = dto.getDescription();
        if (description == null || !descriptionMatcher.reset(description).matches()) {
            return false;
        }
        BigDecimal price = dto.getPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0 || price.compareTo(MAX_PRICE) > 0) {
            return false;
        }
        Integer duration = dto.getDuration();
        if (duration == null || duration <= 0 || duration > MAX_DURATION) {
            return false;
        }
        Set<TagDto> tags = dto.getTags();
        return tags != null && tags.stream().allMatch(this::validateTagDto);
    }

    @Override
    public boolean validateCertificateDtoUpdate(CertificateDto dto) {
        String name = dto.getName();
        if (name != null && !certificateNameMatcher.reset(name).matches()) {
            return false;
        }
        String description = dto.getDescription();
        if (description != null && !descriptionMatcher.reset(description).matches()) {
            return false;
        }
        BigDecimal price = dto.getPrice();
        if (price != null && (price.compareTo(BigDecimal.ZERO) <= 0 || price.compareTo(MAX_PRICE) > 0)) {
            return false;
        }
        Integer duration = dto.getDuration();
        if (duration != null && (duration <= 0 || duration > MAX_DURATION)) {
            return false;
        }
        Set<TagDto> tags = dto.getTags();
        if (tags != null && tags.stream()
                .anyMatch(t -> !validateTagDto(t))) {
            return false;
        }
        return name != null ||
                description != null ||
                price != null ||
                duration != null ||
                tags != null;
    }

    @Override
    public boolean validateSearchParameters(Map<String, String> parameters) {
        if (parameters.isEmpty()) {
            return false;
        }
        Set<String> paramNames = SearchParameterName.getAllParamNames();
        Set<String> keys = parameters.keySet();
        Optional<String> wrongParamName = keys.stream()
                .filter(s -> !paramNames.contains(s))
                .findAny();
        if (wrongParamName.isPresent()) {
            return false;
        }
        String tag = parameters.get(TAG);
        if (tag != null && !tagNameMatcher.reset(tag).matches()) {
            return false;
        }
        String name = parameters.get(NAME);
        if (name != null && !certificateNameMatcher.reset(name).matches()) {
            return false;
        }
        String description = parameters.get(DESCRIPTION);
        if (description != null && !descriptionMatcher.reset(description).matches()) {
            return false;
        }
        String sortBy = parameters.get(SORT_BY);
        return sortBy == null || validateSortBy(sortBy);

    }

    private boolean validateSortBy(String sortBy) {
        if (sortBy == null) {
            return false;
        }
        String[] types = sortBy.split(SORTING_TYPE_SPLITTER);
        Set<String> sortingTypes = EnumSet.allOf(SortingType.class).stream()
                .map(SortingType::getType)
                .collect(Collectors.toSet());
        return Stream.of(types).allMatch(sortingTypes::contains);
    }
}
