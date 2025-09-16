package com.example.demo.controller;

import com.example.demo.dto.TransactionRequest;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.dto.TransferRequest;
import com.example.demo.dto.TransferResponse;
import com.example.demo.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/v1/transaction")
public class TransactionController {
    /*
    * transfer
    * hesaptaki tüm işlemleri çekelim
    *
    *
     */

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody @Valid TransferRequest transferRequest) {
        var tx = transactionService.transferIslemi(transferRequest);
        return ResponseEntity.created(URI.create("/v1/transaction/" + tx.out().transactionId() )).body(tx);
    }

    @PostMapping("/paraYatirma")
    public ResponseEntity<TransactionResponse> paraYatirma(@RequestBody @Valid TransactionRequest request) {
        return ResponseEntity.ok(transactionService.paraYatirmaIslemi(request.accountId() , request.amount() , request.desc()));
    }
    @PostMapping("/paraCekme")
    public ResponseEntity<TransactionResponse> paraCekme(@RequestBody @Valid TransactionRequest request) {
        return ResponseEntity.ok(transactionService.paraCekmeIslemi(request.accountId() , request.amount() , request.desc()));
    }


    
}
