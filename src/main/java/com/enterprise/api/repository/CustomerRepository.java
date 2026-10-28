package com.enterprise.api.repository;

import com.enterprise.api.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    /**
     * Prevents N+1 query problem by eagerly fetching the accounts collection
     * in a single optimized SQL JOIN query.
     */
    @NonNull
    @Override
    @EntityGraph(attributePaths = {"accounts"})
    Optional<Customer> findById(@NonNull Long id);

    /**
     * EntityGraph applied during paginated specification queries.
     */
    @NonNull
    @Override
    @EntityGraph(attributePaths = {"accounts"})
    Page<Customer> findAll(@Nullable Specification<Customer> spec, @NonNull Pageable pageable);

    boolean existsByEmail(String email);

    Optional<Customer> findByEmail(String email);
}
