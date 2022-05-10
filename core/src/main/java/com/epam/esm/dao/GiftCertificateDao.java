package com.epam.esm.dao;

import com.epam.esm.dao.entity.GiftCertificate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface add BaseDao for work with GiftCertificate entity
 */
public interface GiftCertificateDao extends BaseDao<GiftCertificate, Long> {

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
