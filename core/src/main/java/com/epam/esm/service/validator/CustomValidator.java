package com.epam.esm.service.validator;

import com.epam.esm.enumeration.SearchParameterName;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.RegistrationFormDto;
import com.epam.esm.service.dto.TagDto;

import java.util.List;
import java.util.Map;

/**
 * Class to validate data
 */
public interface CustomValidator {

    /**
     * Method to validate entity id
     *
     * @param id entity id
     * @return true - if data is valid, false - if not
     */
    boolean validateEntityId(Long id);

    /**
     * Method to validate CustomTag blank
     *
     * @param tag CustomTag blank
     * @return true - if data is valid, false - if not
     */
    boolean validateTagDto(TagDto tag);

    /**
     * Method to validate GiftCertificateDto blank before create new GiftCertificate
     *
     * @param giftCertificateDto GiftCertificateDto blank
     * @return true - if data is valid, false - if not
     */
    boolean validateCertificateDtoCreate(CertificateDto giftCertificateDto);

    /**
     * Method to validate GiftCertificateDto blank before update GiftCertificate
     *
     * @param giftCertificateDto GiftCertificateDto blank
     * @return true - if data is valid, false - if not
     */
    boolean validateCertificateDtoUpdate(CertificateDto giftCertificateDto);

    /**
     * Method to validate search parameters
     *
     * @param parameters search parameters
     * @return true - if data is valid, false - if not
     */
    boolean validateSearchParameters(Map<SearchParameterName, String> parameters);

    /**
     * Method to validate username
     *
     * @param username user name
     * @return true - if data is valid, false - if not
     */
    boolean validateUsername (String username);

    /**
     * Method to validate registration form
     *
     * @param registrationForm RegistrationFormDto
     * @return true - if data is valid, false - if not
     */
    boolean validateRegistrationForm (RegistrationFormDto registrationForm);

    /**
     * Method to validate page and size
     *
     * @param page - page
     * @param size - page size
     * @return true - if data is valid, false - if not
     */
    boolean validatePageSize (int page, int size);

    /**
     * Method to validate CertificateDto list
     *
     * @param certificates - CertificateDto list
     * @return true - if data is valid, false - if not
     */
    boolean validateCertificateList (List<CertificateDto> certificates);
}
