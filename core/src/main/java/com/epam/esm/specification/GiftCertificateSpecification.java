package com.epam.esm.specification;

import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.dao.entity.CustomTag_;
import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.dao.entity.GiftCertificate_;
import com.epam.esm.enumeration.SearchParameterName;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Class represent custom GiftCertificate specification.
 */
public class GiftCertificateSpecification implements Specification<GiftCertificate> {

    private static final String PERCENT_SIGN = "%";
    private SearchCriteria criteria;

    public GiftCertificateSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<GiftCertificate> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        SearchParameterName key = criteria.getKey();
        Predicate predicate;
        switch (key) {
            case TAG -> {
                Join<GiftCertificate, CustomTag> joinTags = root.join(GiftCertificate_.tags);
                predicate = builder.equal(joinTags.get(CustomTag_.name), criteria.getValue());
            }
            case NAME -> predicate = builder
                    .like(root.get(GiftCertificate_.name), PERCENT_SIGN + criteria.getValue() + PERCENT_SIGN);
            case DESCRIPTION -> predicate = builder
                    .like(root.get(GiftCertificate_.description), PERCENT_SIGN + criteria.getValue() + PERCENT_SIGN);
            case ACTIVE -> {
                int tinyintValue = Boolean.TRUE.toString().equals(criteria.getValue()) ? 1 : 0;
                predicate = builder
                        .equal(root.get(GiftCertificate_.active), tinyintValue);
            }
            default -> predicate = null;
        }
        return predicate;
    }
}
