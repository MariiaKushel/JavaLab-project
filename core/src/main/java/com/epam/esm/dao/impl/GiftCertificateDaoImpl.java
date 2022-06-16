package com.epam.esm.dao.impl;

import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.dao.entity.CustomTag_;
import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.dao.entity.GiftCertificate_;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
public class GiftCertificateDaoImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public List<GiftCertificate> findAllByTagsNamesAndActive(String[] tagsNames, boolean active, Pageable paging) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteria = builder.createQuery(GiftCertificate.class);
        Root<GiftCertificate> root = criteria.from(GiftCertificate.class);
        Join<GiftCertificate, CustomTag> joinTags = root.join(GiftCertificate_.tags);

        int numberOfTags = tagsNames.length;
        criteria.select(root)
                .where(
                        joinTags.get(CustomTag_.name).in(tagsNames),
                        builder.equal(root.get(GiftCertificate_.active), active))
                .groupBy(root.get(GiftCertificate_.id))
                .having(builder.equal(builder.count(root.get(GiftCertificate_.id)), numberOfTags));
        int page = paging.getPageNumber();
        int size = paging.getPageSize();
        return entityManager.createQuery(criteria)
                .setFirstResult((page) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public int countByTagsNamesAndActive(String[] tagsNames, boolean active) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteria = builder.createQuery(GiftCertificate.class);
        Root<GiftCertificate> root = criteria.from(GiftCertificate.class);
        Join<GiftCertificate, CustomTag> joinTags = root.join(GiftCertificate_.tags);

        int numberOfTags = tagsNames.length;
        criteria.select(root)
                .where(
                        joinTags.get(CustomTag_.name).in(tagsNames),
                        builder.equal(root.get(GiftCertificate_.active), active))
                .groupBy(root.get(GiftCertificate_.id))
                .having(builder.equal(builder.count(root.get(GiftCertificate_.id)), numberOfTags));
        return entityManager.createQuery(criteria)
                .getResultList()
                .size();
    }
}
