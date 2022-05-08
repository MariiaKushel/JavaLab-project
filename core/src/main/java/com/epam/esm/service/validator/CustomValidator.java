package com.epam.esm.service.validator;

import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.TagDto;

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
    boolean validateSearchParameters(Map<String, String> parameters);
}
