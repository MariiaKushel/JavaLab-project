package com.epam.esm.specification;

import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.enumeration.SearchParameterName;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

/**
 * Class helps to create a specification.
 */
public class SpecificationCreator {

    /**
     * Method convert search parameters map to specification.
     *
     * @param parameters search parameters map
     * @return GiftCertificate specification
     */
    public static Specification<GiftCertificate> getSpecification(Map<SearchParameterName, String> parameters) {
        List<SearchParameterName> keys = parameters.keySet().stream()
                .sorted((o1, o2) -> -1 * (o1.name().compareTo(o2.name()))).toList();
        Specification<GiftCertificate> specification = null;
        for (SearchParameterName key : keys) {
            if (key == SearchParameterName.SORT_BY) {
                continue;
            }
            SearchCriteria criteria = new SearchCriteria(key, parameters.get(key));
            GiftCertificateSpecification spec = new GiftCertificateSpecification(criteria);
            if (specification == null) {
                specification = Specification.where(spec);
            } else {
                specification = specification.and(spec);
            }
        }
        return specification;
    }
}
