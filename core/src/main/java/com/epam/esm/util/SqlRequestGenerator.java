package com.epam.esm.util;

import com.epam.esm.dao.ColumnName;
import com.epam.esm.service.SearchParameterName;
import com.epam.esm.service.SortingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Util class for construct sql request and data for that request
 */
public class SqlRequestGenerator {
    private static final String SQL_UPDATE = "UPDATE gift_certificate SET ";
    private static final String NAME = "name=?, ";
    private static final String DESCRIPTION = "description=?, ";
    private static final String PRICE = "price=?, ";
    private static final String DURATION = "duration=?, ";
    private static final String LAST_UPDATE_DATE = "last_update_date=? WHERE id_gift_certificate=?";

    private static final String SQL_SELECT = """
            SELECT gift_certificate.id_gift_certificate, gift_certificate.name, description, price, duration, create_date, last_update_date
            FROM gift_certificate
            """;
    private static final String TAG = """
            JOIN (
                SELECT certificate_tag.id_gift_certificate AS gift_id, tag.id_tag, tag.name
                FROM tag
                JOIN certificate_tag ON tag.name=? AND certificate_tag.id_tag=tag.id_tag
            ) AS temp_table ON gift_certificate.id_gift_certificate=gift_id             
            """;
    private static final String WHERE = " WHERE";
    private static final String NAME_PART = " gift_certificate.name LIKE ? ";
    private static final String AND = " AND";
    private static final String DESCRIPTION_PART = " description LIKE ? ";
    private static final String ORDER_BY_NAME_ASC = " ORDER BY gift_certificate.name ASC";
    private static final String ORDER_BY_NAME_DESC = " ORDER BY gift_certificate.name DESC";
    private static final String ORDER_BY_DATE_ASC = " ORDER BY create_date ASC";
    private static final String ORDER_BY_DATE_DESC = " ORDER BY create_date DESC";
    private static final String ORDER_BY_NAME_ASC_DATE_DESC = " ORDER BY create_date DESC, gift_certificate.name ASC";
    private static final String PERCENT_SIGN = "%";

    /**
     * Method generate sql request and data for update operation
     * @param parameters parameters of request
     * @return map consist 1 recode, where key - sql request as string and list of values for prepare statement
     */
    public static Map<String, List<String>> generateSqlUpdateData(Map<String, String> parameters) {
        StringBuilder sqlRequest = new StringBuilder(SQL_UPDATE);
        List<String> values = new ArrayList<>();
        String name = parameters.get(ColumnName.NAME_GIFT_CERTIFICATE);
        if (name!= null && !"null".equals(name)) {
            sqlRequest.append(NAME);
            values.add(name);
        }
        String description = parameters.get(ColumnName.DESCRIPTION);
        if (description != null && !"null".equals(description)) {
            sqlRequest.append(DESCRIPTION);
            values.add(description);
        }
        String price = parameters.get(ColumnName.PRICE);
        if (price!=null && !"null".equals(price)) {
            sqlRequest.append(PRICE);
            values.add(price);
        }
        String duration = parameters.get(ColumnName.DURATION);
        if (duration != null && !"null".equals(duration)) {
            sqlRequest.append(DURATION);
            values.add(duration);
        }
        sqlRequest.append(LAST_UPDATE_DATE);

        Map<String, List<String>> requestData = new HashMap<>();
        requestData.put(sqlRequest.toString(), values);
        return requestData;
    }

    /**
     * Method generate sql request and data for search by parameters operation
     * @param parameters search parameters
     * @return map consist 1 recode, where key - sql request as string and list of values for prepare statement
     */
    public static Map<String, List<String>> generateSqlSelectByParametersData(Map<String, String> parameters) {
        StringBuilder sqlRequest = new StringBuilder(SQL_SELECT);
        List<String> values = new ArrayList<>();
        String tag = parameters.get(SearchParameterName.TAG);
        if (tag != null) {
            sqlRequest.append(TAG);
            values.add(tag);
        }
        String name = parameters.get(SearchParameterName.NAME);
        String description = parameters.get(SearchParameterName.DESCRIPTION);
        if (name != null || description != null) {
            sqlRequest.append(WHERE);
            if (name != null) {
                sqlRequest.append(NAME_PART);
                values.add(PERCENT_SIGN + name + PERCENT_SIGN);
                if (description != null) {
                    sqlRequest.append(AND);
                    sqlRequest.append(DESCRIPTION_PART);
                    values.add(PERCENT_SIGN + description + PERCENT_SIGN);
                }
            } else {
                sqlRequest.append(DESCRIPTION_PART);
                values.add(PERCENT_SIGN + description + PERCENT_SIGN);
            }
        }
        String sorting = parameters.get(SearchParameterName.SORTING);
        if (sorting != null) {
            sqlRequest.append(chooseSortingRequestPart(sorting));
        }
        Map<String, List<String>> requestData = new HashMap<>();
        requestData.put(sqlRequest.toString(), values);
        return requestData;
    }

    /**
     * Method chooses part of sql request for sorting type
     * @param sorting sorting type as string
     * @return part of sql request
     */
    private static String chooseSortingRequestPart(String sorting) {
        SortingType type = SortingType.valueOf(sorting.toUpperCase());
        return switch (type) {
            case NAME_ASC -> ORDER_BY_NAME_ASC;
            case NAME_DESC -> ORDER_BY_NAME_DESC;
            case DATE_ASC -> ORDER_BY_DATE_ASC;
            case DATE_DESC -> ORDER_BY_DATE_DESC;
            case NAME_ASC_DATE_DESC -> ORDER_BY_NAME_ASC_DATE_DESC;
        };
    }
}
