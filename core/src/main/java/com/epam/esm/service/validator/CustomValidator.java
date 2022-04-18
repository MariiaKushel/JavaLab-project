package com.epam.esm.service.validator;

import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.service.dto.GiftCertificateDto;

import java.util.Map;

/**
 * Class to validate data
 */
public interface CustomValidator{

    /**
     * Method to validate entity id
     * @param id entity id
     * @return true - if data is valid, false - if not
     */
    boolean validateEntityId (long id);

    /**
     * Method to validate CustomTag blank
     * @param tag CustomTag blank
     * @return true - if data is valid, false - if not
     */
    boolean validateCustomTag(CustomTag tag);

    /**
     * Method to validate GiftCertificateDto blank before create new GiftCertificate
     * @param giftCertificateDto GiftCertificateDto blank
     * @return true - if data is valid, false - if not
     */
    boolean validateGiftCertificateDtoCreate (GiftCertificateDto giftCertificateDto);

    /**
     * Method to validate GiftCertificateDto blank before update GiftCertificate
     * @param giftCertificateDto GiftCertificateDto blank
     * @return true - if data is valid, false - if not
     */
    boolean validateGiftCertificateDtoUpdate (GiftCertificateDto giftCertificateDto);

    /**
     * Method to validate search parameters
     * @param parameters search parameters
     * @return true - if data is valid, false - if not
     */
    boolean validateSearchParameters (Map<String, String>parameters);
}
