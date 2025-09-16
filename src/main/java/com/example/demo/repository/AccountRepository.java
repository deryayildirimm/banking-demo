package com.example.demo.repository;

import com.example.demo.model.Account;
import com.example.demo.model.AccountStatus;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")) // 5sn bekle
    Optional<Account> lockByIdForUpdate(Long id);

    // okurken kimse yazmasın
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> lockByIdForRead(Long id);


    Long countAccountByCustomer_Id(Long customerId);

    Page<Account> findAccountsByCustomer_Id(Long customerId, Pageable pageable);

    boolean existsByCustomer_IdAndBalanceNot(Long customerId, BigDecimal balance);

    List<Account> findAllByCustomer_IdAndStatus(Long customerId, AccountStatus status);



}
