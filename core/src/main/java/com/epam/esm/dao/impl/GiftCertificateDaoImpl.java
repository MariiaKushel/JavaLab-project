package com.epam.esm.dao.impl;

import com.epam.esm.dao.ColumnName;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.util.SqlRequestGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

@Repository
public class GiftCertificateDaoImpl implements GiftCertificateDao {

    private static final String SQL_SELECT_ALL = """
            SELECT id_gift_certificate, name, description, price, duration, create_date, last_update_date
            FROM gift_certificate
            """;

    private static final String SQL_SELECT_BY_ID = """
            SELECT id_gift_certificate, name, description, price, duration, create_date, last_update_date
            FROM gift_certificate
            WHERE id_gift_certificate=?
            """;

    private static final String SQL_INSERT = """
            INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date)
            VALUES (?,?,?,?,?,?)
            """;

    private static final String SQL_INSERT_COUPLING = """
            INSERT INTO certificate_tag (id_gift_certificate, id_tag)
            VALUES (?,?)
            """;

    private static final String SQL_DELETE = """
            DELETE FROM gift_certificate
            WHERE id_gift_certificate=?
            """;

    private static final String SQL_DELETE_COUPLING = """
            DELETE FROM certificate_tag 
            WHERE id_gift_certificate=?""";


    private JdbcTemplate jdbcTemplate;

    @Autowired
    public GiftCertificateDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public GiftCertificate save(GiftCertificate entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = (connection) -> {
            PreparedStatement prepareStatement = connection.prepareStatement(SQL_INSERT, RETURN_GENERATED_KEYS);
            prepareStatement.setString(1, entity.getName());
            prepareStatement.setString(2, entity.getDescription());
            prepareStatement.setBigDecimal(3, entity.getPrice());
            prepareStatement.setInt(4, entity.getDuration());
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            prepareStatement.setTimestamp(5, now);
            prepareStatement.setTimestamp(6, now);
            return prepareStatement;
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        long generatedKey = keyHolder.getKey().longValue();
        return findById(generatedKey).get();
    }

    @Override
    public GiftCertificate update(Map<String, String> parameters) {
        Map<String, List<String>> requestData = SqlRequestGenerator.generateSqlUpdateData(parameters);
        String sqlUpdate = requestData.keySet().stream().findFirst().get();
        List<String> values = requestData.get(sqlUpdate);
        Long id = Long.parseLong(parameters.get(ColumnName.ID_GIFT_CERTIFICATE));

        PreparedStatementCreator preparedStatementCreator = (connection) -> {
            PreparedStatement prepareStatement = connection.prepareStatement(sqlUpdate);
            int pos = 1;
            for (int i = 0; i < values.size(); i++, pos++) {
                prepareStatement.setString(pos, values.get(i));
            }
            prepareStatement.setTimestamp(pos, Timestamp.valueOf(LocalDateTime.now()));
            prepareStatement.setLong(pos + 1, id);
            return prepareStatement;
        };

        jdbcTemplate.update(preparedStatementCreator);
        return findById(id).get();
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update(SQL_DELETE_COUPLING, id);
        jdbcTemplate.update(SQL_DELETE, id);
    }

    @Override
    public List<GiftCertificate> findAll() {
        return jdbcTemplate.query(SQL_SELECT_ALL, mapper);
    }

    @Override
    public Optional<GiftCertificate> findById(Long id) {
        List<GiftCertificate> giftCertificateList = jdbcTemplate.query(SQL_SELECT_BY_ID, mapper, id);
        return giftCertificateList.stream().findFirst();
    }

    @Override
    public boolean saveCoupling(Long giftCertificateId, Long customTagId) {
        int insertedRows = jdbcTemplate.update(SQL_INSERT_COUPLING, giftCertificateId, customTagId);
        return insertedRows == 1;
    }

    @Override
    public List<GiftCertificate> findAllByParameters(Map<String, String> parameters) {
        Map<String, List<String>> requestData = SqlRequestGenerator.generateSqlSelectByParametersData(parameters);
        String sqlSelect = requestData.keySet().stream().findFirst().get();
        String[] values = requestData.get(sqlSelect).stream().toArray(String[]::new);
        return jdbcTemplate.query(sqlSelect, mapper, values);
    }
}
