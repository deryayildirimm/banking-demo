package com.example.demo.repository;

import com.example.demo.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findTop5ByAccount_IdOrderByCreatedTimeDesc(Long accountId);

    Long countByAccount_Id(Long accountId);
    Optional<Transaction> findFirstByAccount_IdOrderByCreatedTimeDesc(Long accountId);

}
