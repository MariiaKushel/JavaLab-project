package com.epam.esm.dao;

import com.epam.esm.dao.entity.GiftCertificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interface for database operation with GiftCertificate entity
 */
public interface GiftCertificateDao extends PagingAndSortingRepository<GiftCertificate, Long>,
        JpaSpecificationExecutor<GiftCertificate> {

    /**
     * Find GiftCertificate by id and active
     *
     * @param id     GiftCertificate id
     * @param active GiftCertificate active status
     * @return Optional representation of GiftCertificate or empty Optional, if GiftCertificate was not found
     */
    Optional<GiftCertificate> findByIdAndActive(long id, boolean active);

    /**
     * Find all GiftCertificates by active
     *
     * @param active GiftCertificate active status
     * @param paging pagination parameters
     * @return Page of GiftCertificates or empty Page if no one GiftCertificate was not found
     */
    Page<GiftCertificate> findAllByActive(boolean active, Pageable paging);

    /**
     * Count all GiftCertificates by active
     *
     * @param active GiftCertificate active status
     * @return GiftCertificates quantity
     */
    long countByActive(boolean active);

    /**
     * Find all GiftCertificates by specification
     *
     * @param specification search parameters
     * @param paging        pagination parameters
     * @return Page of GiftCertificates or empty Page if no one GiftCertificate was not found
     */
    Page<GiftCertificate> findAll(Specification<GiftCertificate> specification, Pageable paging);

    /**
     * Count all GiftCertificates by specification
     *
     * @param specification search parameters
     * @return GiftCertificates quantity
     */
    long count(Specification<GiftCertificate> specification);

    /**
     * Find all GiftCertificates by several tags names (“and” condition) and active
     *
     * @param tagsNames tags names
     * @param active    GiftCertificate active status
     * @param paging    pagination parameters
     * @return List of GiftCertificates or empty List if no one GiftCertificate was not found
     */
    List<GiftCertificate> findAllByTagsNamesAndActive(String[] tagsNames, boolean active, Pageable paging);


    /**
     * Count all GiftCertificates by several tags names (“and” condition)
     *
     * @param tagsNames tags names
     * @param active    GiftCertificate active status
     * @return GiftCertificates quantity
     */
    int countByTagsNamesAndActive(String[] tagsNames, boolean active);

    /**
     * Find GiftCertificate by name and description and price and duration and active
     *
     * @param name        GiftCertificate name
     * @param description GiftCertificate description
     * @param price       GiftCertificate price
     * @param duration    GiftCertificate duration
     * @param active      GiftCertificate active status
     * @return Optional representation of GiftCertificate or empty Optional, if GiftCertificate was not found
     */
    Optional<GiftCertificate> findByNameAndDescriptionAndPriceAndDurationAndActive(String name,
                                                                                   String description,
                                                                                   BigDecimal price,
                                                                                   int duration,
                                                                                   boolean active);
}
