package com.epam.esm.dao;

import com.epam.esm.dao.entity.GiftCertificate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import static com.epam.esm.dao.ColumnName.*;

/**
 * Interface add BaseDao for work with GiftCertificate entity
 */
public interface GiftCertificateDao extends BaseDao<GiftCertificate, Long> {

    /**
     * RowMapper for GiftCertificate
     */
    RowMapper<GiftCertificate> mapper = (ResultSet resultSet, int rowNum) -> {
        GiftCertificate certificate = GiftCertificate.newBuilder()
                .setEntityId(resultSet.getLong(ID_GIFT_CERTIFICATE))
                .setName(resultSet.getString(NAME_GIFT_CERTIFICATE))
                .setDescription(resultSet.getString(DESCRIPTION))
                .setPrice(resultSet.getBigDecimal(PRICE))
                .setDuration(resultSet.getInt(DURATION))
                .setCreateDate(resultSet.getTimestamp(CREATE_DATE).toLocalDateTime())
                .setLastUpdateDate(resultSet.getTimestamp(LAST_UPDATE_DATE).toLocalDateTime())
                .build();
        return certificate;
    };

    /**
     * Save coupling between GiftCertificate and CustomTag
     * @param giftCertificateId GiftCertificate id
     * @param customTagId CustomTags id
     * @return true - if the operation was successful, false - if the operation was failed
     */
    boolean saveCoupling(Long giftCertificateId, Long customTagId);

    /**
     * Find GiftCertificates by parameters
     * @param parameters search parameters
     * @return list of GiftCertificates or empty list if no one GiftCertificate was not found
     */
    List<GiftCertificate> findAllByParameters(Map<String, String> parameters);

}
