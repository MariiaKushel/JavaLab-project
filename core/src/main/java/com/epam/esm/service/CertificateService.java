package com.epam.esm.service;

import com.epam.esm.enumeration.SearchParameterName;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.dto.CertificateDto;

import java.util.List;
import java.util.Map;

/**
 * Interface contains service methods for work with GiftCertificate entity
 */
public interface CertificateService {

    /**
     * Find active GiftCertificate by id
     *
     * @param id GiftCertificate id
     * @return GiftCertificate as GiftCertificateDto
     * @throws CustomException if id has not valid value or GiftCertificate was not found
     */
    CertificateDto findById(long id) throws CustomException;

    /**
     * Find all active GiftCertificate with pagination
     *
     * @param page page
     * @param size page size
     * @return GiftCertificate list as GiftCertificateDto list or empty list if no one GiftCertificate was not found
     * @throws CustomException if page or size have not valid value
     */
    List<CertificateDto> findAll(int page, int size) throws CustomException;

    /**
     * Delete GiftCertificate by id. If GiftCertificate has links to another resource, it will deactivate.
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
     * @throws CustomException if dto has not valid value or such GiftCertificate already exist
     *                         or CustomTag by id was not found
     */
    CertificateDto create(CertificateDto certificateDto) throws CustomException;

    /**
     * Partly update active GiftCertificate
     * Also create new CustomTags if it needed and create coupling between GiftCertificate and CustomTags
     *
     * @param certificateDto blank Of GiftCertificate fields to update as GiftCertificateDto
     * @return updated GiftCertificate as GiftCertificateDto
     * @throws CustomException if id or dto have not valid value or GiftCertificate or CustomTag were not found
     */
    CertificateDto update(long id, CertificateDto certificateDto) throws CustomException;

    /**
     * Find active GiftCertificates by parameters with pagination
     *
     * @param page       page
     * @param size       page size
     * @param parameters search parameters
     * @return list of GiftCertificates as GiftCertificateDto or empty list if no one GiftCertificate was not found
     * @throws CustomException if parameters, page or size have not valid value
     */
    List<CertificateDto> findAllByParameters(Map<SearchParameterName, String> parameters, int page, int size)
            throws CustomException;

    /**
     * Find active GiftCertificates by tags with pagination
     *
     * @param page page
     * @param size page size
     * @param tags tags names for search
     * @return list of GiftCertificates as GiftCertificateDto or empty list if no one GiftCertificate was not found
     * @throws CustomException if tags, page or size have not valid value
     */
    List<CertificateDto> findAllByTags(String[] tags, int page, int size) throws CustomException;

    /**
     * Find the last page by all active GiftCertificates
     *
     * @param size page size
     * @return last page value
     * @throws CustomException if size has not valid value
     */
    int findAllLastPage(int size) throws CustomException;

    /**
     * Find the last page by all active GiftCertificates by parameters
     *
     * @param parameters search parameters
     * @param size       page size
     * @return last page value
     * @throws CustomException if parameters or size have not valid value
     */
    int findAllByParametersLastPage(Map<SearchParameterName, String> parameters, int size) throws CustomException;


    /**
     * Find the last page by all active GiftCertificates by tags names
     *
     * @param tags tags names for search
     * @param size page size
     * @return last page value
     * @throws CustomException if tags name or size have not valid value
     */
    int findAllByTagsLastPage(String[] tags, int size) throws CustomException;

}
