package com.epam.esm.service.validator.impl;

import com.epam.esm.enumeration.SearchParameterName;
import com.epam.esm.enumeration.SortingType;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.RegistrationFormDto;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.validator.CustomValidator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CustomValidatorImpl implements CustomValidator {

    private static final Long MAX_ID = (long) Math.pow(2, 63);
    private static final BigDecimal MAX_PRICE = new BigDecimal("999999.99");
    private static final int MAX_DURATION = 180;
    private static final String SORTING_TYPE_SPLITTER = ",";

    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("\\w+");
    private static final Pattern CERTIFICATE_NAME_PATTERN = Pattern.compile("[\\w\\s]+");
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("[\\p{Graph}\\s]+");
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("([\\d\\p{Lower}_\\-\\.]+)@[\\d\\p{Lower}_\\-]{2,}\\.\\p{Lower}{2,6}");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("[\\p{Graph}&&[^\\<\\>]]{1,8}");
    private static final Pattern NAME_OF_USER_PATTERN = Pattern.compile("[\\w\\s]+");

    private static final Matcher tagNameMatcher = TAG_NAME_PATTERN.matcher("");
    private static final Matcher certificateNameMatcher = CERTIFICATE_NAME_PATTERN.matcher("");
    private static final Matcher descriptionMatcher = DESCRIPTION_PATTERN.matcher("");
    private static final Matcher usernameMatcher = USERNAME_PATTERN.matcher("");
    private static final Matcher passwordMatcher = PASSWORD_PATTERN.matcher("");
    private static final Matcher nameOfUserMatcher = NAME_OF_USER_PATTERN.matcher("");

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
        return tags != null && tags.stream().allMatch(t -> t.getId() != null || validateTagDto(t));
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
                .anyMatch(t -> t.getId() == null && !validateTagDto(t))) {
            return false;
        }
        return name != null ||
                description != null ||
                price != null ||
                duration != null ||
                tags != null;
    }

    @Override
    public boolean validateSearchParameters(Map<SearchParameterName, String> parameters) {
        if (parameters.isEmpty()) {
            return false;
        }

        String tag = parameters.get(SearchParameterName.TAG);
        if (tag != null && !tagNameMatcher.reset(tag).matches()) {
            return false;
        }
        String name = parameters.get(SearchParameterName.NAME);
        if (name != null && !certificateNameMatcher.reset(name).matches()) {
            return false;
        }
        String description = parameters.get(SearchParameterName.DESCRIPTION);
        if (description != null && !descriptionMatcher.reset(description).matches()) {
            return false;
        }
        String sortBy = parameters.get(SearchParameterName.SORT_BY);//fixme change sort
        return sortBy == null || validateSortBy(sortBy);

    }

    @Override
    public boolean validateUsername(String username) {
        return username != null && usernameMatcher.reset(username).matches();
    }

    @Override
    public boolean validateRegistrationForm(RegistrationFormDto registrationForm) {
        String password = registrationForm.getPassword();
        String name = registrationForm.getName();
        if (password == null || !passwordMatcher.reset(password).matches()) {
            return false;
        }
        return name != null && nameOfUserMatcher.reset(name).matches();
    }

    @Override
    public boolean validatePageSize(int page, int size) {
        return page > 0 && size > 0;
    }

    @Override
    public boolean validateCertificateList(List<CertificateDto> certificates) {
        if (certificates.isEmpty()) {
            return false;
        }
        boolean isValidIds = certificates.stream()
                .map(CertificateDto::getId)
                .allMatch(this::validateEntityId);
        if (!isValidIds) {
            return false;
        }
        return certificates.stream()
                .map(CertificateDto::getPrice)
                .allMatch(p -> p != null && p.compareTo(BigDecimal.ZERO) > 0 && p.compareTo(MAX_PRICE) <= 0);
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
