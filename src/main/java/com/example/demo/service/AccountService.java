package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exception.*;
import com.example.demo.model.Account;
import com.example.demo.model.AccountStatus;
import com.example.demo.model.Customer;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;


@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionService transactionService;


    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository,
                          TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionService = transactionService;
    }


    public AccountResponse createAccount(AccountRequest accountRequest) {

        Customer customer = customerRepository.findById(accountRequest.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(accountRequest.customerId()));

        Account accountNew = Account.open(
                accountRequest.balance(),
                customer
        );

      Account saved =   accountRepository.save(accountNew);
      Customer customer1 = saved.getCustomer();
      CustomerBrief customerBrief = toCustomerBrief(customer1);

        return new AccountResponse(
                saved.getId(),
                saved.getBalance() ,
                saved.getCreatedAt().toString(),
                customerBrief ,
                0L
                );
    }


    public AccountResponse getAccountDetail(Long id) {
        var account = getAccountById(id);

        Customer customer = Objects.requireNonNull(account.getCustomer(), "Account.customer is null");
        Instant createdAt = Objects.requireNonNull(account.getCreatedAt(), "Account.createdAt is null");

        var customerBrief = toCustomerBrief(customer);

        var totalTransaction = transactionService.totalTransactionForAccount(account.getId());

        List<TransactionBrief> transactionBriefList = transactionService.last5ForAccount(account.getId());

        return toAccountResponse(account, totalTransaction, transactionBriefList, customerBrief);

    }

    public void deleteAccount(Long id) {

        Account account = getAccountById(id);
        ThrowExceptionHandler.throwIf(account.getBalance().compareTo(BigDecimal.ZERO) != 0 ,
                () -> new BalanceNotZeroException(account.getBalance()));

        ThrowExceptionHandler.throwIf(account.getStatus().equals(AccountStatus.CLOSED),
                AccountAlreadyClosedException::new);

        account.close();
        accountRepository.save(account);

    }

    public Page<AccountResponse> getAllAccounts(Pageable pageable) {

        Page<Account> accountPage = accountRepository.findAll(pageable);

                return accountPage.map(  account ->
                        toAccountResponse(account, transactionService.totalTransactionForAccount(account.getId()),
                                null, toCustomerBrief(account.getCustomer()))
                    );

    }

    public Long accountNumberForCustomer(Long customerId) {
        return accountRepository.countAccountByCustomer_Id(customerId);
    }

    public Page<AccountBrief> getAllAccountsforCustomer (Long customerId, Pageable pageable) {
      Page<Account> accountPage=  accountRepository.findAccountsByCustomer_Id(customerId, pageable);
        return accountPage.map(  account -> new AccountBrief(
                account.getBalance(),
                account.getCreatedAt(),
                toCustomerBrief(account.getCustomer())
        ));
    }

    private CustomerBrief toCustomerBrief(Customer c) {
        return new CustomerBrief(
                c.getId(),
                c.getName(),
                c.getPhone(),
                c.getEmail()
        );
    }

    private AccountResponse toAccountResponse(Account a, long totalTrx,
                                              List<TransactionBrief> transactionBriefList,
                                              CustomerBrief customerBrief ) {
            return new AccountResponse(
                    a.getId(),
                    a.getBalance(),
                    a.getCreatedAt().toString(),
                    customerBrief,
                    totalTrx,
                    transactionBriefList
            );
    }

    private Account getAccountById(Long id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new AccountNotFoundException(id));
    }


}
