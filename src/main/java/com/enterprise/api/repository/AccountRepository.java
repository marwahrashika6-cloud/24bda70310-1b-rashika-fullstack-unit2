package com.enterprise.api.repository;

import com.enterprise.api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    List<Account> findByCustomerId(Long customerId);

    /**
     * Prevents lazy loading N+1 penalty by executing an explicit JOIN FETCH for the customer association.
     */
    @Query("SELECT a FROM Account a JOIN FETCH a.customer WHERE a.id = :id")
    Optional<Account> findByIdWithCustomer(@Param("id") Long id);
}
