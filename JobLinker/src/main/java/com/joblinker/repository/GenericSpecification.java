package com.joblinker.repository;

import com.joblinker.domain.dto.SearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class GenericSpecification<T> implements Specification<T> {
    private SearchCriteria criteria;

    public GenericSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        // Check for null
        if (criteria == null) {
            return builder.conjunction();
        }

        switch (criteria.getOperation()) {
            case ">":
                return builder.greaterThanOrEqualTo(
                        root.get(criteria.getKey()).as(String.class), criteria.getValue().toString());

            case "<":
                return builder.lessThanOrEqualTo(
                        root.get(criteria.getKey()).as(String.class), criteria.getValue().toString());

            case ":":
                if (root.get(criteria.getKey()).getJavaType() == String.class) {
                    return builder.like(
                            root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
                } else {
                    return builder.equal(root.get(criteria.getKey()), criteria.getValue());
                }

            default:
                // Handle unsupported operations
                return null;
        }
    }

    public SearchCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(SearchCriteria criteria) {
        this.criteria = criteria;
    }
}
