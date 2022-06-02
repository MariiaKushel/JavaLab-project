package com.epam.esm.dao.impl;

import com.epam.esm.dao.entity.CustomTag;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class CustomTagDaoImpl {

    private static final String SQL_SELECT_THE_MOST_WIDELY_TAG = """
            SELECT tags.id, tags.name
            FROM
            (
            	SELECT gift_certificates_tags.id_tag AS id_tag, COUNT(gift_certificates_tags.id_tag) AS num_tag
            	FROM
            	(
                   		SELECT orders.id_user AS id_user, SUM(orders.amount) AS total_sum
            			FROM orders					
            			GROUP BY orders.id_user
            			ORDER BY total_sum DESC, id_user
            		    LIMIT 1
            	) AS needed_user
            	JOIN orders ON needed_user.id_user=orders.id_user
                JOIN orders_gift_certificates ON orders.id = orders_gift_certificates.id_order
            	JOIN gift_certificates_tags ON orders_gift_certificates.id_gift_certificate = gift_certificates_tags.id_gift_certificate
            	GROUP BY gift_certificates_tags.id_tag
            	ORDER BY num_tag DESC, id_tag
            	LIMIT 1
            ) AS needed_tag
            JOIN tags ON tags.id = needed_tag.id_tag
            """;

    @PersistenceContext
    private EntityManager entityManager;

    public CustomTag findTheMostWidelyTag() {
        return (CustomTag) entityManager.createNativeQuery(SQL_SELECT_THE_MOST_WIDELY_TAG, CustomTag.class)
                .getSingleResult();
    }
}
