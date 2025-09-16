package com.example.demo.service;

import com.example.demo.dto.AccountBrief;
import com.example.demo.dto.AccountRequest;
import com.example.demo.dto.AccountResponse;
import com.example.demo.dto.TransactionBrief;
import com.example.demo.exception.*;
import com.example.demo.model.Account;
import com.example.demo.model.AccountStatus;
import com.example.demo.model.Customer;
import com.example.demo.model.TransactionType;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class AccountServiceTest {

    private AccountService accountService;
    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private TransactionService transactionService;



    @BeforeEach
    public void setUp() {

        accountRepository = mock(AccountRepository.class);
        customerRepository = mock(CustomerRepository.class);
        transactionService = mock(TransactionService.class);

        accountService = new AccountService(accountRepository, customerRepository, transactionService);


    }

    @Test
    void shouldReturnAccountResponse_whenCustomerExistsAndBalancePositive(){
        //  her sey dogru gıdıp save etmelı
        // account response donmelı
        //
        long customerId = 11L;
        var req = new AccountRequest(new BigDecimal("100.0") , customerId);

        // customer var
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(customerId);
        when(customer.getName()).thenReturn("John Doe");
        when(customer.getPhone()).thenReturn("1234567890");
        when(customer.getEmail()).thenReturn("john.doe@example.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // account repoya save edılıyo
        Account saved = mock(Account.class);
        when(saved.getId()).thenReturn(100L);
        when(saved.getBalance()).thenReturn(new BigDecimal("100.00"));
        when(saved.getCustomer()).thenReturn(customer);
        when(saved.getCreatedAt()).thenReturn(Instant.parse("2025-01-01T00:00:00Z"));
        when(accountRepository.save(any(Account.class))).thenReturn(saved);

        // test ettıgım kısım action
        AccountResponse response = accountService.createAccount(req);

        assertEquals(100L, response.id());
        assertEquals(0, response.balance().compareTo(new BigDecimal("100.00")));
        assertEquals(customerId, response.customerBrief().id());
        assertEquals("John Doe", response.customerBrief().name());
        assertEquals("1234567890", response.customerBrief().phone());
        assertEquals("john.doe@example.com", response.customerBrief().email());
        assertEquals(0L, response.transactionCount());

        verify(customerRepository, times(1)).findById(customerId);
        verify(accountRepository, times(1)).save(any(Account.class)); // varsayılan times(1)

    }

    @Test
    void shouldThrowAnException_whenCustomerDoesNotExist(){

        long customerId = 11L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        var req = new AccountRequest(new BigDecimal("100.0") , customerId);

        assertThrows(CustomerNotFoundException.class , () -> accountService.createAccount(req));

        verifyNoInteractions(accountRepository);

    }

    @Test
    void shoudlThrownAnException_whenBalanceNegative(){
        // customer ım var donuyor garantiledim
        // ama balance negative ve cidden enetity de bu hata bana service e gelıyor mu
        long customerId = 11L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mock(Customer.class)));
        assertThrows(NotEnoughBalanceException.class ,
                () -> accountService.createAccount(new AccountRequest(new BigDecimal("-1") , customerId)));

        verifyNoInteractions(accountRepository);

    }

    @Test
    void shouldThrownAnException_whenRepositorySaveFailed(){
        long customerId = 11L;

        var req = new AccountRequest(new BigDecimal("100.0") , customerId);

        var customer = mock(Customer.class);
        when(customer.getId()).thenReturn(customerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        when(accountRepository.save(any(Account.class))).thenThrow(
                new RuntimeException("couldn't save this account for now")
        );
        assertThrows(RuntimeException.class , () -> accountService.createAccount(req));

        InOrder inOrder = inOrder(customerRepository, accountRepository);
        inOrder.verify(customerRepository).findById(customerId);
        inOrder.verify(accountRepository).save(any(Account.class));
    }


    @Test
    void shouldReturnDetail_whenEverythingIsOk(){

        long accountId = 11L;

        Account account = mock(Account.class);
        when(account.getId()).thenReturn(accountId);
        when(account.getCreatedAt()).thenReturn(Instant.parse("2025-01-01T00:00:00Z"));
        when(account.getBalance()).thenReturn(new BigDecimal("300.00"));

        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(5L);
        when(customer.getName()).thenReturn("John Doe");
        when(customer.getPhone()).thenReturn("1234567890");
        when(customer.getEmail()).thenReturn("john.doe@example.com");

        when(account.getCustomer()).thenReturn(customer);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        when(transactionService.totalTransactionForAccount(accountId)).thenReturn(5L);
        List<TransactionBrief> last5 = List.of(
                new TransactionBrief(1L, TransactionType.DEPOSIT, new BigDecimal("10"), "2025-01-02T00:00:00Z"),
                new TransactionBrief(2L, TransactionType.TRANSFER_OUT, new BigDecimal("22"), "2025-02-02T00:00:00Z")
        );
        when(transactionService.last5ForAccount(accountId)).thenReturn(last5);

        var response = accountService.getAccountDetail(accountId);

        assertEquals(accountId, response.id());
        assertEquals(5L, response.customerBrief().id());
        assertEquals("John Doe", response.customerBrief().name());
        assertEquals("1234567890", response.customerBrief().phone());
        assertEquals("john.doe@example.com", response.customerBrief().email());
        assertEquals(5L, response.transactionCount());
        assertEquals(new BigDecimal("300.00"), response.balance());
        assertEquals(2, response.transactionBriefList().size());

        verify(accountRepository, times(1)).findById(accountId);
        verify(transactionService, times(1)).totalTransactionForAccount(accountId);
        verify(transactionService, times(1)).last5ForAccount(accountId);

    }

    @Test
    void shouldThrownAnAccountNotFoundException_whenAccountNotFound(){
        long accountId = 11L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class , () -> accountService.getAccountDetail(accountId));

        verify(accountRepository, times(1)).findById(accountId);
        verifyNoInteractions(transactionService);

    }

    @Test
    void shouldThrowAnException_whenTransactionPartFailed(){

        long accountId = 11L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mock(Account.class)));

        when(transactionService.totalTransactionForAccount(accountId)).thenThrow(
                new RuntimeException("transaction parts failed")
        );

        assertThrows(RuntimeException.class , () -> accountService.getAccountDetail(accountId));

        verify(accountRepository, times(1)).findById(accountId);

    }

    @Test
    void shouldThrowAnException_whenLast5TrxListFailed(){
        long accountId = 11L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mock(Account.class)));
        when(transactionService.totalTransactionForAccount(accountId)).thenReturn(15L);

        when(transactionService.last5ForAccount(accountId)).thenThrow(new RuntimeException("transaction list part failed"));

        assertThrows(RuntimeException.class , () -> accountService.getAccountDetail(accountId));

        verify(accountRepository, times(1)).findById(accountId);

    }


    // ---------------------------
    // deleteAccount(Long id)  (soft delete)
    // ---------------------------

    @Test
    void shouldCloseStatus_whenAccountOpenAndBalanceZero(){
        long accountId = 11L;

       Account account = mock(Account.class);
       when(account.getId()).thenReturn(accountId);
       when(account.getStatus()).thenReturn(AccountStatus.OPEN);
       when(account.getBalance()).thenReturn(BigDecimal.ZERO);

       when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

       accountService.deleteAccount(accountId);


       verify(account).close();
       verify(accountRepository, times(1)).save(account);

    }

    @Test
    void shouldThrowAnException_whenAccountOpenAndBalanceNotZero(){
        long accountId = 11L;
        Account account = mock(Account.class);
        when(account.getId()).thenReturn(accountId);
        when(account.getStatus()).thenReturn(AccountStatus.OPEN);
        when(account.getBalance()).thenReturn(new BigDecimal("100.00"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(BalanceNotZeroException.class , () -> accountService.deleteAccount(accountId));

        verify(account,never()).close();
        verify(accountRepository,never()).save(account);


    }

    @Test
    void shouldThrowAnException_whenBalanceZeroAndAccountNotFound(){
        long accountId = 11L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class , () -> accountService.getAccountDetail(accountId));

        verify(accountRepository,times(1)).findById(accountId);
        verifyNoInteractions(transactionService);

    }

    @Test
    void shouldThrownAnException_whenAccountClosedAndBalanceZero(){
        long accountId = 11L;
        Account account = mock(Account.class);
        when(account.getId()).thenReturn(accountId);
        when(account.getStatus()).thenReturn(AccountStatus.CLOSED);
        when(account.getBalance()).thenReturn(BigDecimal.ZERO);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(AccountAlreadyClosedException.class , () -> accountService.deleteAccount(accountId));

        verify(accountRepository,times(1)).findById(accountId);
        verify(accountRepository,never()).save(account);
        verifyNoInteractions(transactionService);

    }

    @Test
    void shouldThrownAnException_whenRepositorySaveFails(){
        long accountId = 11L;

        Account account = mock(Account.class);
        when(account.getId()).thenReturn(accountId);
        when(account.getStatus()).thenReturn(AccountStatus.OPEN);
        when(account.getBalance()).thenReturn(BigDecimal.ZERO);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

      when(accountRepository.save(account)).thenThrow(new RuntimeException("save failed"));

      assertThrows(RuntimeException.class , () -> accountService.deleteAccount(accountId));

        // behavior: sırayı ve yan etkileri doğrula
        InOrder inOrder = inOrder(accountRepository, account);
        inOrder.verify(accountRepository).findById(accountId);
        inOrder.verify(account).getBalance();
        inOrder.verify(account).getStatus();
        inOrder.verify(account).close();
        inOrder.verify(accountRepository).save(account);
        verifyNoInteractions(transactionService);

        verifyNoMoreInteractions(accountRepository , account);

    }

    @Test
    void shouldReturnAccountResponsePage_whenAccountExists(){

        Pageable pageable = PageRequest.of(0, 2);

        // Account + Customer mockları
        Customer c1 = mock(Customer.class);
        when(c1.getId()).thenReturn(101L);
        when(c1.getName()).thenReturn("Ada");
        when(c1.getPhone()).thenReturn("555");
        when(c1.getEmail()).thenReturn("ada@example.com");

        Account a1 = mock(Account.class);
        when(a1.getId()).thenReturn(1L);
        when(a1.getBalance()).thenReturn(new BigDecimal("10.00"));
        when(a1.getCreatedAt()).thenReturn(Instant.parse("2025-01-02T00:00:00Z"));
        when(a1.getCustomer()).thenReturn(c1);

        Customer c2 = mock(Customer.class);
        when(c2.getId()).thenReturn(202L);
        when(c2.getName()).thenReturn("Can");
        when(c2.getPhone()).thenReturn("444");
        when(c2.getEmail()).thenReturn("can@example.com");

        Account a2 = mock(Account.class);
        when(a2.getId()).thenReturn(2L);
        when(a2.getBalance()).thenReturn(new BigDecimal("20.50"));
        when(a2.getCreatedAt()).thenReturn(Instant.parse("2025-01-03T00:00:00Z"));
        when(a2.getCustomer()).thenReturn(c2);

        when(accountRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(a1, a2), pageable, 2));

        when(transactionService.totalTransactionForAccount(1L)).thenReturn(3L);
        when(transactionService.totalTransactionForAccount(2L)).thenReturn(5L);

        Page<AccountResponse> pageResponse = accountService.getAllAccounts(pageable);

        assertEquals(2, pageResponse.getContent().size());
        AccountResponse r1 = pageResponse.getContent().get(0);
        assertEquals(1L, r1.id());
        assertEquals(0, r1.balance().compareTo(new BigDecimal("10.00")));
        assertEquals("2025-01-02T00:00:00Z", r1.createdAt());
        assertEquals(3L, r1.transactionCount());
        assertEquals(101L, r1.customerBrief().id());
        assertEquals("Ada", r1.customerBrief().name());

        AccountResponse r2 = pageResponse.getContent().get(1);
        assertEquals(2L, r2.id());
        assertEquals(0, r2.balance().compareTo(new BigDecimal("20.50")));
        assertEquals("2025-01-03T00:00:00Z", r2.createdAt());
        assertEquals(5L, r2.transactionCount());
        assertEquals(202L, r2.customerBrief().id());
        assertEquals("Can", r2.customerBrief().name());

        verify(accountRepository).findAll(pageable);
        verify(transactionService).totalTransactionForAccount(1L);
        verify(transactionService).totalTransactionForAccount(2L);
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    void shouldReturnEmptyPage_whenNoAccounts(){

        Pageable pageable = PageRequest.of(0, 5);
        when(accountRepository.findAll(pageable))
                .thenReturn(Page.empty(pageable));

        Page<AccountResponse> pageResponse = accountService.getAllAccounts(pageable);

        assertEquals(0, pageResponse.getContent().size());

        verify(accountRepository).findAll(pageable);
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(transactionService);

    }


    @Test
    void shouldReturnCount_whenAccountExists(){

        Long customerId = 18L;

        when(accountRepository.countAccountByCustomer_Id(customerId)).thenReturn(3L);

        Long numberAccounts = accountService.accountNumberForCustomer(customerId);

        assertEquals(3L, numberAccounts);
        verify(accountRepository).countAccountByCustomer_Id(customerId);

    }

    @Test
    void shouldThrownException_whenCannotConnectToDB(){

        Long customerId = 18L;
        when(accountRepository.countAccountByCustomer_Id(customerId)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> accountService.accountNumberForCustomer(customerId));

        verify(accountRepository).countAccountByCustomer_Id(customerId);

    }

    @Test
    void shouldReturnAccountPage_forSpecificCustomer(){
        Long customerId = 18L;
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(customerId);
        when(customer.getPhone()).thenReturn("444");
        when(customer.getEmail()).thenReturn("ada@example.com");
        when(customer.getName()).thenReturn("Ada");
        Pageable pageable = PageRequest.of(0, 2);

        Account account1 = mock(Account.class);
        when(account1.getId()).thenReturn(1L);
        when(account1.getCreatedAt()).thenReturn(Instant.parse("2025-08-02T00:00:00Z"));
        when(account1.getStatus()).thenReturn(AccountStatus.OPEN);
        when(account1.getBalance()).thenReturn(new BigDecimal("20.50"));
        when(account1.getCustomer()).thenReturn(customer);

        Account account2 = mock(Account.class);
        when(account2.getId()).thenReturn(2L);
        when(account2.getBalance()).thenReturn(new BigDecimal("24.50"));
        when(account2.getCreatedAt()).thenReturn(Instant.parse("2025-01-02T00:00:00Z"));
        when(account2.getCustomer()).thenReturn(customer);

        when(accountRepository.findAccountsByCustomer_Id(customerId, pageable)).thenReturn(
                new PageImpl<>(List.of(account1, account2), pageable, 2)
        );

        Page<AccountBrief> pageResponse = accountService.getAllAccountsforCustomer(customerId, pageable);

        assertEquals(2, pageResponse.getContent().size());

        AccountBrief brief = pageResponse.getContent().get(0);
        assertEquals(18L , brief.customer().id());
        assertEquals("Ada", brief.customer().name());
        assertEquals("444" , brief.customer().phone());
        assertEquals("ada@example.com" , brief.customer().email());
        assertEquals(0 , brief.balance().compareTo(new BigDecimal("20.50")));
        assertEquals(Instant.parse("2025-08-02T00:00:00Z"), brief.createdAt());

        AccountBrief brief2 = pageResponse.getContent().get(1);
        assertEquals(18L , brief2.customer().id());
        assertEquals("Ada", brief2.customer().name());
        assertEquals("444" , brief2.customer().phone());
        assertEquals("ada@example.com" , brief2.customer().email());
        assertEquals(0 , brief2.balance().compareTo(new BigDecimal("24.50")));
        assertEquals(Instant.parse("2025-01-02T00:00:00Z"), brief2.createdAt());

        verify(accountRepository).findAccountsByCustomer_Id(customerId, pageable);
        verifyNoMoreInteractions(accountRepository);


    }

    @Test
    void shouldThrownAnError_whenSmtWrongWithDB(){
        Long customerId = 18L;

        Pageable pageable = PageRequest.of(0, 2);
        when(accountRepository.findAccountsByCustomer_Id(customerId, pageable)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> accountService.getAllAccountsforCustomer(customerId, pageable));

        verify(accountRepository).findAccountsByCustomer_Id(customerId, pageable);

    }

    @Test
    void shouldReturnEmptyPage_whenNoAccountsFound(){
        Long customerId = 18L;
        Pageable pageable = PageRequest.of(0, 2);

        when(accountRepository.findAccountsByCustomer_Id(customerId, pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

        Page<AccountBrief> page = accountService.getAllAccountsforCustomer(customerId, pageable);

        assertEquals(0, page.getContent().size());

        verify(accountRepository).findAccountsByCustomer_Id(customerId, pageable);

    }


    @AfterEach
    public void tearDown() {

    }



}
