package com.epam.esm.dao.impl;

import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.entity.CustomTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

@Repository
public class CustomTagDaoImpl implements CustomTagDao {

    private static final String SQL_SELECT_BY_ID = "SELECT id_tag, name FROM tag WHERE id_tag=?";
    private static final String SQL_SELECT_BY_NAME = "SELECT id_tag, name FROM tag WHERE name=?";
    private static final String SQL_SELECT_ALL = "SELECT id_tag, name FROM tag ";
    private static final String SQL_DELETE = "DELETE FROM tag WHERE id_tag=?";
    private static final String SQL_DELETE_COUPLING = "DELETE FROM certificate_tag WHERE id_tag=?";
    private static final String SQL_INSERT = "INSERT INTO tag (name) VALUES (?)";
    private static final String SQL_SELECT_ALL_BY_GIFT_CERTIFICATE_ID = """
            SELECT tag.id_tag, name 
            FROM certificate_tag
            JOIN tag ON id_gift_certificate=? AND tag.id_tag=certificate_tag.id_tag 
            """;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CustomTagDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CustomTag save(CustomTag entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = (connection) -> {
            PreparedStatement prepareStatement = connection.prepareStatement(SQL_INSERT, RETURN_GENERATED_KEYS);
            prepareStatement.setString(1, entity.getName());
            return prepareStatement;
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        long generatedKey = keyHolder.getKey().longValue();
        return findById(generatedKey).get();
    }

    @Override
    public CustomTag update(Map<String, String> parameters) {
        throw new UnsupportedOperationException("Invalid operation \"update\" for CustomTag");
    }

    @Override
    @Transactional
    public void delete(Long id) {
        jdbcTemplate.update(SQL_DELETE_COUPLING, id);
        jdbcTemplate.update(SQL_DELETE, id);
    }

    @Override
    public List<CustomTag> findAll() {
        return jdbcTemplate.query(SQL_SELECT_ALL, mapper);
    }

    @Override
    public Optional<CustomTag> findById(Long id) {
        List<CustomTag> customTagList = jdbcTemplate.query(SQL_SELECT_BY_ID, mapper, id);
        return customTagList.stream().findFirst();
    }

    @Override
    public List<CustomTag> findAllByGiftCertificateId(long giftCertificateId) {
        return jdbcTemplate.query(SQL_SELECT_ALL_BY_GIFT_CERTIFICATE_ID, mapper, giftCertificateId);
    }

    @Override
    public Optional<CustomTag> findByName(String name) {
        List<CustomTag> customTagList = jdbcTemplate.query(SQL_SELECT_BY_NAME, mapper, name);
        return customTagList.stream().findFirst();
    }
}
