package com.example.demo.service;

import com.example.demo.dto.AccountBrief;
import com.example.demo.dto.CustomerResponse;
import com.example.demo.exception.CustomerHasActiveBalanceException;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.exception.ThrowExceptionHandler;
import com.example.demo.model.AccountStatus;
import com.example.demo.model.Customer;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class CustomerService {
    /*
    * tum customerların gelmesi
    * customer ı silme işlemi
    * customer ın account bilgilerinin gelmesi
    * customer detay bılgısı
     */

    private final CustomerRepository customerRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    public CustomerService(CustomerRepository customerRepository,
                           AccountService accountService,
                           AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    @Transactional
   public void deleteCustomer(Long id ) {

        Customer customer = findByCustomerId(id);

        ThrowExceptionHandler.throwIf(accountRepository.existsByCustomer_IdAndBalanceNot(id, BigDecimal.ZERO),
                () -> new CustomerHasActiveBalanceException(id));

        accountRepository.findAllByCustomer_IdAndStatus(id, AccountStatus.OPEN)
                .forEach(a -> accountService.deleteAccount(a.getId()));

        customer.softDelete();
        customerRepository.save(customer);
   }

   public CustomerResponse getCustomerDetails(Long id) {
        /*
         * Customer -> id, name, address, phone , email , totalAccountNumber
         */
        // first -> check the customer
        Customer customer = findByCustomerId(id);
        Long totalAccount = accountService.accountNumberForCustomer(customer.getId());

        return toCustomerResponse(customer, totalAccount);
   }


   public Page<CustomerResponse> getAllCustomers(Pageable pageable) {

        Page<Customer> customers = customerRepository.findAll(pageable);

        return customers.map(customer ->
                toCustomerResponse(customer, accountService.accountNumberForCustomer(customer.getId())
        ));
   }

   public Page<AccountBrief> accountsOf(Long customerId, Pageable pageable) {
        return accountService.getAllAccountsforCustomer(customerId, pageable);
   }

    private CustomerResponse toCustomerResponse(Customer customer , Long totalAccount){
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getAddress(),
                customer.getPhone(),
                customer.getEmail(),
                totalAccount
        );
    }

    private Customer findByCustomerId(Long id) {
        return customerRepository.findById(id)
                .orElseThrow( () ->  new CustomerNotFoundException(id));
    }


}
