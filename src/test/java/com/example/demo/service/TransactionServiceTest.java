package com.example.demo.service;

import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        accountRepository = mock(AccountRepository.class);
        transactionService = new TransactionService(transactionRepository, accountRepository);
    }




    @AfterEach
    public void tearDown() {

    }

}
