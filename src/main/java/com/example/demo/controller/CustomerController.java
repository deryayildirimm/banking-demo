package com.example.demo.controller;

import com.example.demo.dto.AccountBrief;
import com.example.demo.dto.CustomerResponse;
import com.example.demo.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping
    public Page<CustomerResponse> customerList( @Valid
            @PageableDefault(page = 1, size = 10, sort = "name") Pageable pageable) {

        return customerService.getAllCustomers(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@Valid @PathVariable Long id) {

        CustomerResponse customerResponse = customerService.getCustomerDetails(id);
        return ResponseEntity.ok(customerResponse);
    }

    @GetMapping("/{id}/accounts")
    public Page<AccountBrief> getAccounts( @Valid @PathVariable Long id,
                                                @PageableDefault(page = 1, size = 10)
                                                Pageable pageable) {
        return customerService.accountsOf(id, pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable  @Valid Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }






}
