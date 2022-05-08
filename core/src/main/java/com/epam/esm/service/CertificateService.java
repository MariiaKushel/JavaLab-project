package com.epam.esm.service;

import com.epam.esm.exception.CustomException;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.TagDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface contains service methods for work with GiftCertificate entity
 */
public interface CertificateService {

    /**
     * Find GiftCertificate by id
     *
     * @param id GiftCertificate id
     * @return GiftCertificate as GiftCertificateDto
     * @throws CustomException if id has not valid value or GiftCertificate was not found
     */
    CertificateDto findById(long id) throws CustomException;

    /**
     * Find all GiftCertificate with pagination
     *
     * @param page page
     * @param size page size
     * @return GiftCertificate list as GiftCertificateDto list
     * or empty list if no one GiftCertificate was not found
     * @throws CustomException if page or size has not valid value
     */
    List<CertificateDto> findAll(int page, int size) throws CustomException;

    /**
     * Delete GiftCertificate by id
     *
     * @param id GiftCertificate id
     * @throws CustomException if id has not valid value or GiftCertificate was not found.
     */
    void delete(long id) throws CustomException;

    /**
     * Create new GiftCertificate.
     * Also create new CustomTags if it needed and create coupling between GiftCertificate and CustomTags
     *
     * @param certificateDto blank Of GiftCertificate as GiftCertificateDto
     * @return new GiftCertificate as GiftCertificateDto
     * @throws CustomException if id has not valid value or such certificate already exist
     */
    CertificateDto create(CertificateDto certificateDto) throws CustomException;

    /**
     * Partly update GiftCertificate
     * Also create new CustomTags if it needed and create coupling between GiftCertificate and CustomTags
     *
     * @param certificateDto blank Of GiftCertificate fields to update as GiftCertificateDto
     * @return updated GiftCertificate as GiftCertificateDto
     * @throws CustomException if id has not valid value or GiftCertificate was not found
     */
    CertificateDto update(long id, CertificateDto certificateDto) throws CustomException;

    /**
     * Update GiftCertificate name
     *
     * @param id   GiftCertificate id
     * @param name new name
     * @return updated GiftCertificate as GiftCertificateDto
     * @throws CustomException if id or name has not valid value or GiftCertificate was not found
     */
    CertificateDto updateName(long id, String name) throws CustomException;

    /**
     * Update GiftCertificate description
     *
     * @param id          GiftCertificate id
     * @param description new description
     * @return updated GiftCertificate as GiftCertificateDto
     * @throws CustomException if id or description has not valid value or GiftCertificate was not found
     */
    CertificateDto updateDescription(long id, String description) throws CustomException;

    /**
     * Update GiftCertificate price
     *
     * @param id    GiftCertificate id
     * @param price new price
     * @return updated GiftCertificate as GiftCertificateDto
     * @throws CustomException if id or price has not valid value or GiftCertificate was not found
     */
    CertificateDto updatePrice(long id, BigDecimal price) throws CustomException;

    /**
     * Update GiftCertificate duration
     *
     * @param id       GiftCertificate id
     * @param duration new duration
     * @return updated GiftCertificate as GiftCertificateDto
     * @throws CustomException if id or duration has not valid value or GiftCertificate was not found
     */
    CertificateDto updateDuration(long id, Integer duration) throws CustomException;

    /**
     * Update GiftCertificate tags
     *
     * @param id      GiftCertificate id
     * @param tagDtos new tags
     * @return updated GiftCertificate as GiftCertificateDto
     * @throws CustomException if id or tags has not valid value or GiftCertificate was not found
     */
    CertificateDto updateTags(long id, Set<TagDto> tagDtos) throws CustomException;

    /**
     * Find GiftCertificates by parameters with pagination
     *
     * @param page       page
     * @param size       page size
     * @param parameters search parameters
     * @return list of GiftCertificates as GiftCertificateDto or empty list if no one GiftCertificate was not found
     */
    List<CertificateDto> findAllByParameters(Map<String, String> parameters, int page, int size) throws CustomException;

    /**
     * Find GiftCertificates by tags with pagination
     *
     * @param page page
     * @param size page size
     * @param tags tags for search
     * @return list of GiftCertificates as GiftCertificateDto or empty list if no one GiftCertificate was not found
     */
    List<CertificateDto> findByTags(String[] tags, int page, int size) throws CustomException;

    /**
     * Count all GiftCertificates
     *
     * @return quantity of GiftCertificates
     */
    long count();

    /**
     * Count all GiftCertificates by parameters
     *
     * @return quantity of GiftCertificates
     */
    long countByParameters(Map<String, String> parameters) throws CustomException;

    /**
     * Count all GiftCertificates by tags
     *
     * @return quantity of GiftCertificates
     */
    long countByTags(String[] tags) throws CustomException;

}
