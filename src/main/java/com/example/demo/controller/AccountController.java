package com.example.demo.controller;

import com.example.demo.dto.AccountRequest;
import com.example.demo.dto.AccountResponse;
import com.example.demo.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody @Valid AccountRequest accountRequest) {

       return ResponseEntity.ok(accountService.createAccount(accountRequest));
    }

    @GetMapping
    public Page<AccountResponse> getAccountList(
            @PageableDefault(page = 1, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return accountService.getAllAccounts(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable @Valid Long id) {

        return ResponseEntity.ok(accountService.getAccountDetail(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable @Valid Long id) {

        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }


}
