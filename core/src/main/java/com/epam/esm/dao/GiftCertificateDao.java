package com.epam.esm.dao;

import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.dao.entity.GiftCertificate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Interface add BaseDao for work with GiftCertificate entity
 */
public interface GiftCertificateDao extends BaseDao<GiftCertificate, Long> {

    /**
     * Update fully GiftCertificate
     *
     * @param giftCertificate entity to update
     * @return GiftCertificates after updating
     */
    GiftCertificate update(GiftCertificate giftCertificate);

    /**
     * Update GiftCertificate name
     *
     * @param name new name
     * @return GiftCertificates after updating
     */
    GiftCertificate updateName(Long id, String name);

    /**
     * Update GiftCertificate description
     *
     * @param description new description
     * @return GiftCertificates after updating
     */
    GiftCertificate updateDescription(Long id, String description);

    /**
     * Update GiftCertificate price
     *
     * @param price new price
     * @return GiftCertificates after updating
     */
    GiftCertificate updatePrice(Long id, BigDecimal price);

    /**
     * Update GiftCertificate duration
     *
     * @param duration new duration
     * @return GiftCertificates after updating
     */
    GiftCertificate updateDuration(Long id, Integer duration);

    /**
     * Update GiftCertificate tags
     *
     * @param tags new tag set
     * @return GiftCertificates after updating
     */
    GiftCertificate updateTags(Long id, Set<CustomTag> tags);

    /**
     * Find GiftCertificates by parameters
     *
     * @param page       page
     * @param size       page size
     * @param parameters search parameters
     * @return list of GiftCertificates or empty list if no one GiftCertificate was not found
     */
    List<GiftCertificate> findAllByParameters(Map<String, String> parameters, int page, int size);

    /**
     * Count all GiftCertificates into database by parameters
     *
     * @return quantity of all GiftCertificates
     */
    long countByParameters(Map<String, String> parameters);

    /**
     * Find GiftCertificates by tags
     *
     * @param page page
     * @param size page size
     * @param tags tags
     * @return list of GiftCertificates or empty list if no one GiftCertificate was not found
     */
    List<GiftCertificate> findByTags(int page, int size, String... tags);

    /**
     * Count all GiftCertificates into database by tags
     *
     * @return quantity of all GiftCertificates
     */
    long countByTags(String[] tags);

    Optional<GiftCertificate> findNameAndDescriptionAndPriceAndDuration(String name, String description,
                                                                        BigDecimal price, int duration);
}
