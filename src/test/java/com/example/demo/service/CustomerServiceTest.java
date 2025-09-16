package com.example.demo.service;

import com.example.demo.dto.AccountBrief;
import com.example.demo.dto.CustomerBrief;
import com.example.demo.dto.CustomerResponse;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.model.Customer;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CustomerRepository;
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

public class CustomerServiceTest {

    private CustomerService customerService;
    private CustomerRepository customerRepository;
    private AccountService accountService;
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() {

        accountService = mock(AccountService.class);
        customerRepository = mock(CustomerRepository.class);
        accountRepository = mock(AccountRepository.class);
        customerService = new CustomerService(customerRepository, accountService, accountRepository);
    }

    @Test
    void shouldReturnCustomerDetails_whenCustomerIdIsValid() {

        Long customerId = 1L;
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(customerId);
        when(customer.getName()).thenReturn("John Doe");
        when(customer.getEmail()).thenReturn("john.doe@example.com");
        when(customer.getPhone()).thenReturn("1234567890");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        when(accountService.accountNumberForCustomer(customerId)).thenReturn(5L);

       CustomerResponse response =  customerService.getCustomerDetails(customerId);

        assertEquals(1L, response.id());
        assertEquals("John Doe", response.name());
        assertEquals("john.doe@example.com", response.email());
        assertEquals("1234567890", response.phone());
        assertEquals(5L, response.accountTotal());

        verify(accountService).accountNumberForCustomer(customerId);
        verify(customerRepository).findById(customerId);

    }

    @Test
    void shouldThrownAnError_whenCustomerIdIsInvalid() {

        long customerId = 1L;
        when(customerRepository.findById(customerId)).thenThrow(CustomerNotFoundException.class);

        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerDetails(customerId));
        verifyNoInteractions(accountService);
    }

    @Test
    void shouldThrowAnError_whenAccountServiceFails() {

        Long customerId = 11L;
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(customerId);

        when(accountService.accountNumberForCustomer(customerId)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> customerService.getCustomerDetails(customerId));

        verify(customerRepository, times(1)).findById(customerId);

    }

    @Test
    void shouldReturnCustomerResponsePage_whenCustomerExist() {

        Pageable pageable = PageRequest.of(0, 2);
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(1L);
        when(customer.getName()).thenReturn("John Doe");
        when(customer.getEmail()).thenReturn("john.doe@example.com");
        when(customer.getPhone()).thenReturn("1234567890");

        Customer customer2 = mock(Customer.class);
        when(customer2.getId()).thenReturn(18L);
        when(customer2.getName()).thenReturn("Jay Z");
        when(customer2.getEmail()).thenReturn("yeezz.jay@example.com");
        when(customer2.getPhone()).thenReturn("1235879123");

        when(customerRepository.findAll(pageable)).thenReturn( new PageImpl<>(
                        List.of(customer, customer2) , pageable, 2
                        )
                );
        when(accountService.accountNumberForCustomer(customer.getId())).thenReturn(5L);
        when(accountService.accountNumberForCustomer(customer2.getId())).thenReturn(15L);

        Page<CustomerResponse> customerResponsePage = customerService.getAllCustomers(pageable);

        assertEquals(2 , customerResponsePage.getContent().size());

        assertEquals( 1L, customerResponsePage.getContent().getFirst().id());
        assertEquals( 5L, customerResponsePage.getContent().getFirst().accountTotal());
        assertEquals( "John Doe", customerResponsePage.getContent().getFirst().name());
        assertEquals( "john.doe@example.com", customerResponsePage.getContent().getFirst().email());
        assertEquals( "1234567890", customerResponsePage.getContent().getFirst().phone());

        assertEquals( 18L, customerResponsePage.getContent().getLast().id());
        assertEquals(15L, customerResponsePage.getContent().getLast().accountTotal());
        assertEquals( "Jay Z", customerResponsePage.getContent().getLast().name());
        assertEquals( "yeezz.jay@example.com", customerResponsePage.getContent().getLast().email());
        assertEquals( "1235879123", customerResponsePage.getContent().getLast().phone());

        verify(customerRepository).findAll(pageable);
        verify(accountService).accountNumberForCustomer(customer.getId());
        verify(accountService).accountNumberForCustomer(customer2.getId());

    }

    @Test
    void shouldThrowAnError_whenAccountServiceFailsForAnyCustomer() {
        Pageable pageable = PageRequest.of(0, 2);

        Customer c1 = mock(Customer.class);
        when(c1.getId()).thenReturn(101L);
        when(c1.getName()).thenReturn("Ada");
        when(c1.getAddress()).thenReturn("Addr-1");
        when(c1.getPhone()).thenReturn("555");
        when(c1.getEmail()).thenReturn("ada@example.com");

        when(customerRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(c1), pageable, 1));

        when(accountService.accountNumberForCustomer(101L))
                .thenThrow(new RuntimeException("svc down"));

        assertThrows(RuntimeException.class,
                () -> customerService.getAllCustomers(pageable));

        InOrder inOrder = inOrder(customerRepository, accountService);
        inOrder.verify(customerRepository).findAll(pageable);
        inOrder.verify(accountService).accountNumberForCustomer(101L);
        verifyNoMoreInteractions(customerRepository, accountService);
    }


    @Test
    void shouldReturnAccountBriefPage_whenCustomerExistAndHasAccounts(){

        Long customerId = 12L;
        Pageable pageable = PageRequest.of(0, 2);

        var brief = new CustomerBrief(customerId, "Ada", "555", "ada@example.com");
        AccountBrief a1 = new AccountBrief(new BigDecimal("10.00"), Instant.parse("2025-01-01T00:00:00Z"), brief);
        AccountBrief a2 = new AccountBrief(new BigDecimal("20.50"), Instant.parse("2025-01-02T00:00:00Z"), brief);

        when(accountService.getAllAccountsforCustomer(customerId, pageable))
                .thenReturn(new PageImpl<>(List.of(a1, a2), pageable, 2));

        Page<AccountBrief> page = customerService.accountsOf(customerId, pageable);

        // ASSERT - içerik aynen döndü mü?
        assertEquals(2, page.getContent().size());
        assertEquals(a1, page.getContent().get(0));
        assertEquals(a2, page.getContent().get(1));
        assertEquals(2, page.getTotalElements());

        verify(accountService).getAllAccountsforCustomer(customerId, pageable);
        verifyNoMoreInteractions(accountService);
        verifyNoInteractions(customerRepository);

    }

    @Test
    void shouldReturnEmptyPage_whenNoAccounts(){
        Long customerId = 12L;
        Pageable pageable = PageRequest.of(0, 2);

        when(accountService.getAllAccountsforCustomer(customerId, pageable))
                .thenReturn(Page.empty());

        Page<AccountBrief> page = customerService.accountsOf(customerId, pageable);

        assertEquals(0, page.getContent().size());
        assertEquals(0, page.getTotalElements());

        verify(accountService).getAllAccountsforCustomer(customerId, pageable);
        verifyNoMoreInteractions(accountService);
        verifyNoInteractions(customerRepository);
    }

    @Test
    void shouldThrownAnException_whenAccountServiceFailed(){

        long customerId = 19L;
        Pageable pageable = PageRequest.of(0, 2);

        when(accountService.getAllAccountsforCustomer(customerId, pageable))
                .thenThrow(new RuntimeException("service down"));

        assertThrows(RuntimeException.class, () -> customerService.accountsOf(customerId, pageable));

        verify(accountService).getAllAccountsforCustomer(customerId, pageable);
        verifyNoMoreInteractions(accountService);
        verifyNoInteractions(customerRepository);
    }






}
