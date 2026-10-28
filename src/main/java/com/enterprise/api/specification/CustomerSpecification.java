package com.enterprise.api.specification;

import com.enterprise.api.dto.request.CustomerSearchCriteria;
import com.enterprise.api.entity.Customer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic Spring Data JPA Specification builder for scalable and multi-criteria customer queries.
 */
public class CustomerSpecification {

    private CustomerSpecification() {
    }

    public static Specification<Customer> buildSpecification(CustomerSearchCriteria criteria) {
        return (root, query, cb) -> {
            if (criteria == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            // Free-text keyword search across firstName, lastName, and email
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String pattern = "%" + criteria.getKeyword().trim().toLowerCase() + "%";
                Predicate firstNameMatch = cb.like(cb.lower(root.get("firstName")), pattern);
                Predicate lastNameMatch = cb.like(cb.lower(root.get("lastName")), pattern);
                Predicate emailMatch = cb.like(cb.lower(root.get("email")), pattern);
                predicates.add(cb.or(firstNameMatch, lastNameMatch, emailMatch));
            }

            // Exact status filter
            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }

            // Balance range filters
            if (criteria.getMinBalance() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("totalBalance"), criteria.getMinBalance()));
            }
            if (criteria.getMaxBalance() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("totalBalance"), criteria.getMaxBalance()));
            }

            // Created date range filters
            if (criteria.getCreatedAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAfter()));
            }
            if (criteria.getCreatedBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), criteria.getCreatedBefore()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
