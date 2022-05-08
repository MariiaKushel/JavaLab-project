package com.epam.esm.dao.impl;

import com.epam.esm.dao.CustomTagDao;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.dao.entity.CustomTag_;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomTagDaoImpl implements CustomTagDao {

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
            			ORDER BY total_sum DESC
            		    LIMIT 1
            	) AS needed_user
            	JOIN orders ON needed_user.id_user=orders.id_user
                JOIN orders_gift_certificates ON orders.id = orders_gift_certificates.id_order
            	JOIN gift_certificates ON orders_gift_certificates.id_gift_certificate = gift_certificates.id
            	JOIN gift_certificates_tags ON gift_certificates.id = gift_certificates_tags.id_gift_certificate
            	GROUP BY gift_certificates_tags.id_tag
            	ORDER BY num_tag DESC
            	LIMIT 1
            ) AS needed_tag
            JOIN tags ON tags.id = needed_tag.id_tag
            """;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public CustomTag save(CustomTag entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public CustomTag update(CustomTag entity) {
        throw new UnsupportedOperationException("Invalid operation \"update\" for CustomTag");
    }

    @Override
    @Transactional
    public void delete(CustomTag entity) {
        CustomTag tag = entityManager.merge(entity);
        entityManager.remove(tag);
    }

    @Override
    public List<CustomTag> findAll(int page, int size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomTag> criteria = builder.createQuery(CustomTag.class);
        Root<CustomTag> root = criteria.from(CustomTag.class);
        criteria.select(root);
        return entityManager.createQuery(criteria)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public Optional<CustomTag> findById(Long id) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomTag> criteria = builder.createQuery(CustomTag.class);
        Root<CustomTag> root = criteria.from(CustomTag.class);
        criteria.select(root)
                .where(builder.equal(root.get(CustomTag_.id), id));
        return entityManager.createQuery(criteria)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public long count() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<CustomTag> root = criteria.from(CustomTag.class);
        criteria.select(builder.count(root.get(CustomTag_.id)));
        return entityManager.createQuery(criteria)
                .getSingleResult();
    }

    @Override
    public Optional<CustomTag> findByName(String name) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomTag> criteria = builder.createQuery(CustomTag.class);
        Root<CustomTag> root = criteria.from(CustomTag.class);
        criteria.select(root)
                .where(builder.equal(root.get(CustomTag_.name), name));
        return entityManager.createQuery(criteria)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public CustomTag findTheMostWidelyTags() {
        return (CustomTag) entityManager.createNativeQuery(SQL_SELECT_THE_MOST_WIDELY_TAG, CustomTag.class)
                .getSingleResult();
    }
}
