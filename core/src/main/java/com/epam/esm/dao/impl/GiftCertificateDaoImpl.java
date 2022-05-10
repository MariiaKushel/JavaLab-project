package com.epam.esm.dao.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.dao.entity.CustomTag_;
import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.dao.entity.GiftCertificate_;
import com.epam.esm.service.SearchParameterName;
import com.epam.esm.service.SortingType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class GiftCertificateDaoImpl implements GiftCertificateDao {
    private static final String PERCENT_SIGN = "%";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public GiftCertificate save(GiftCertificate entity) {
        return entityManager.merge(entity);
    }

    @Override
    @Transactional
    public GiftCertificate update(GiftCertificate entity) {
        return entityManager.merge(entity);
    }

    @Override
    @Transactional
    public void delete(GiftCertificate entity) {
        GiftCertificate giftCertificate = entityManager.merge(entity);
        entityManager.remove(giftCertificate);
    }

    @Override
    public List<GiftCertificate> findAll(int page, int size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteria = builder.createQuery(GiftCertificate.class);
        Root<GiftCertificate> root = criteria.from(GiftCertificate.class);
        criteria.select(root);
        return entityManager.createQuery(criteria)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public Optional<GiftCertificate> findById(Long id) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteria = builder.createQuery(GiftCertificate.class);
        Root<GiftCertificate> root = criteria.from(GiftCertificate.class);
        criteria.select(root)
                .where(builder.equal(root.get(GiftCertificate_.id), id));
        return entityManager.createQuery(criteria)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public long count() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<GiftCertificate> root = criteria.from(GiftCertificate.class);
        criteria.select(builder.count(root.get(GiftCertificate_.id)));
        return entityManager.createQuery(criteria)
                .getSingleResult();
    }

    @Override
    public List<GiftCertificate> findAllByParameters(Map<String, String> parameters, int page, int size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteria = builder.createQuery(GiftCertificate.class);
        Root<GiftCertificate> root = criteria.from(GiftCertificate.class);
        criteria.select(root);
        Predicate[] filters = collectFilters(builder, root, parameters);
        if (filters.length > 0) {
            criteria.where(filters);
        }
        Order[] sorting = collectSorting(builder, root, parameters.get(SearchParameterName.SORT_BY));
        if (sorting.length > 0) {
            criteria.orderBy(sorting);
        }
        return entityManager.createQuery(criteria)
                .setFirstResult(page - 1)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public long countByParameters(Map<String, String> parameters) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<GiftCertificate> root = criteria.from(GiftCertificate.class);
        criteria.select(builder.count(root.get(GiftCertificate_.id)));
        Predicate[] filters = collectFilters(builder, root, parameters);
        if (filters.length > 0) {
            criteria.where(filters);
        }
        return entityManager.createQuery(criteria)
                .getSingleResult();
    }

    @Override
    public List<GiftCertificate> findByTags(int page, int size, String... tags) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteria = builder.createQuery(GiftCertificate.class);
        Root<GiftCertificate> root = criteria.from(GiftCertificate.class);
        Join<GiftCertificate, CustomTag> joinTags = root.join(GiftCertificate_.tags);

        int numberOfTags = tags.length;
        criteria.select(root)
                .where(joinTags.get(CustomTag_.name).in(tags))
                .groupBy(root.get(GiftCertificate_.id))
                .having(builder.equal(builder.count(root.get(GiftCertificate_.id)), numberOfTags));
        return entityManager.createQuery(criteria)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public long countByTags(String[] tags) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteria = builder.createQuery(GiftCertificate.class);
        Root<GiftCertificate> root = criteria.from(GiftCertificate.class);
        Join<GiftCertificate, CustomTag> joinTags = root.join(GiftCertificate_.tags);

        int numberOfTags = tags.length;
        criteria.select(root)
                .where(joinTags.get(CustomTag_.name).in(tags))
                .groupBy(root.get(GiftCertificate_.id))
                .having(builder.equal(builder.count(root.get(GiftCertificate_.id)), numberOfTags));
        return entityManager.createQuery(criteria)
                .getResultList()
                .size();
    }

    @Override
    public Optional<GiftCertificate> findNameAndDescriptionAndPriceAndDuration(String name, String description,
                                                                               BigDecimal price, int duration) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteria = builder.createQuery(GiftCertificate.class);
        Root<GiftCertificate> root = criteria.from(GiftCertificate.class);
        criteria.select(root)
                .where(
                        builder.equal(root.get(GiftCertificate_.name), name),
                        builder.equal(root.get(GiftCertificate_.description), description),
                        builder.equal(root.get(GiftCertificate_.price), price),
                        builder.equal(root.get(GiftCertificate_.duration), duration)
                );
        return entityManager.createQuery(criteria)
                .getResultList()
                .stream()
                .findFirst();
    }

    private Predicate[] collectFilters(CriteriaBuilder builder, Root<GiftCertificate> root,
                                       Map<String, String> parameters) {
        List<Predicate> predicates = new ArrayList<>();

        String tagName = parameters.get(SearchParameterName.TAG);
        if (tagName != null) {
            Join<GiftCertificate, CustomTag> joinTags = root.join(GiftCertificate_.tags);
            predicates.add(builder.equal(joinTags.get(CustomTag_.name), tagName));
        }

        String name = parameters.get(SearchParameterName.NAME);
        if (name != null) {
            predicates.add(builder.like(root.get(GiftCertificate_.name), PERCENT_SIGN + name + PERCENT_SIGN));
        }

        String description = parameters.get(SearchParameterName.DESCRIPTION);
        if (description != null) {
            predicates.add(builder.like(root.get(GiftCertificate_.description), PERCENT_SIGN + description + PERCENT_SIGN));
        }
        Predicate[] predicatesAsArray = new Predicate[predicates.size()];
        predicatesAsArray = predicates.toArray(predicatesAsArray);
        return predicatesAsArray;
    }

    private Order[] collectSorting(CriteriaBuilder builder, Root<GiftCertificate> root, String sortBy) {
        if (sortBy == null) {
            return new Order[]{};
        }
        SortingType type = SortingType.getSortingType(sortBy);
        List<Order> orderByList =
                switch (type) {
                    case NAME_ASC -> Stream.of(builder.asc(root.get(GiftCertificate_.name))).toList();
                    case NAME_DESC -> Stream.of(builder.desc(root.get(GiftCertificate_.name))).toList();
                    case DATE_ASC -> Stream.of(builder.asc(root.get(GiftCertificate_.createDate))).toList();
                    case DATE_DESC -> Stream.of(builder.desc(root.get(GiftCertificate_.createDate))).toList();
                    case DATE_DESC_NAME_ASC -> Stream.of(builder.desc(root.get(GiftCertificate_.createDate)),
                            builder.asc(root.get(GiftCertificate_.name))).toList();

                };
        Order[] orderByArray = new Order[orderByList.size()];
        orderByArray = orderByList.toArray(orderByArray);
        return orderByArray;
    }
}
