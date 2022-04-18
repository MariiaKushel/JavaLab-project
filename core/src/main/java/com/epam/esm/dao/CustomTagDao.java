package com.epam.esm.dao;

import com.epam.esm.dao.entity.CustomTag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.dao.ColumnName.ID_TAG;
import static com.epam.esm.dao.ColumnName.NAME_TAG;

/**
 * Interface add BaseDao for work with CustomTag entity
 */
public interface CustomTagDao extends BaseDao<CustomTag, Long> {

    /**
     * RowMapper for CustomTag
     */
    RowMapper<CustomTag> mapper = (ResultSet resultSet, int rowNum) -> {
        long id = resultSet.getLong(ID_TAG);
        String name = resultSet.getString(NAME_TAG);
        CustomTag tag = new CustomTag(id, name);
        return tag;
    };

    /**
     * Find all CustomTags by GiftCertificate id
     * @param giftCertificateId GiftCertificate id
     * @return list of CustomTags or empty list if no one CustomTag was not found
     */
    List<CustomTag> findAllByGiftCertificateId(long giftCertificateId);

    /**
     * Finad CustomTag by name
     * @param name CustomTag name
     * @return Optional representation of CustomTag or empty Optional, if CustomTag was not found
     */
    Optional<CustomTag> findByName(String name);

}
