package com.epam.esm.service;

import com.epam.esm.exception.CustomException;
import com.epam.esm.service.dto.GiftCertificateDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface contains service methods for work with GiftCertificate entity
 */
public interface GiftCertificateService {

    /**
     * Find GiftCertificate by id
     * @param id GiftCertificate id
     * @return Optional representation of GiftCertificate as GiftCertificateDto
     * or empty Optional, if GiftCertificate was not found
     * @throws CustomException if id has not valid value
     */
    Optional<GiftCertificateDto> findById(long id) throws CustomException;

    /**
     * Find all GiftCertificate
     * @return list of GiftCertificate as GiftCertificateDto
     * or empty list if no one GiftCertificate was not found
     */
    List<GiftCertificateDto>  findAll();

    /**
     * Delete GiftCertificate by id
     * @param id GiftCertificate id
     * @throws CustomException if id has not valid value
     */
    void delete (long id) throws CustomException;

    /**
     * Create new GiftCertificate.
     * Also create new CustomTags if it needed and create coupling between GiftCertificate and CustomTags
     * @param giftCertificateDto blank Of GiftCertificate as GiftCertificateDto
     * @return new GiftCertificate as GiftCertificateDto
     * @throws CustomException if id has not valid value
     */
    GiftCertificateDto create (GiftCertificateDto giftCertificateDto) throws CustomException;

    /**
     * Partly update GiftCertificate
     * Also create new CustomTags if it needed and create coupling between GiftCertificate and CustomTags
     * @param giftCertificateDto blank Of GiftCertificate fields to update as GiftCertificateDto
     * @return updated GiftCertificate as GiftCertificateDto
     * @throws CustomException if id has not valid value or GiftCertificate was not found
     */
    GiftCertificateDto update (long id, GiftCertificateDto giftCertificateDto) throws CustomException;

    /**
     * Find GiftCertificates by parameters
     * @param parameters search parameters
     * @return list of GiftCertificates as GiftCertificateDto or empty list if no one GiftCertificate was not found
     */
    List<GiftCertificateDto> findAllByParameters (Map<String, String> parameters) throws CustomException;

}
