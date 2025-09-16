package com.example.demo.service;

import com.example.demo.dto.TransactionBrief;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.dto.TransferRequest;
import com.example.demo.dto.TransferResponse;
import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.exception.SameAccountTransferException;
import com.example.demo.exception.ThrowExceptionHandler;
import com.example.demo.model.Account;
import com.example.demo.model.Transaction;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    /*
    * deposit -> para yatırma
    * withdraw -> para çekme
    * para transfer işlemi
     */

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    // para yatırma
    @Transactional
    public TransactionResponse paraYatirmaIslemi(Long accountId, BigDecimal amount, String desc) {
        Account account = accountRepository.lockByIdForUpdate(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        account.paraYatirma(amount);
        Transaction transaction = Transaction.paraYatirma(account, amount, desc);
       Transaction newTransaction =  transactionRepository.save(transaction);

       return new TransactionResponse(
               newTransaction.getId(),
               newTransaction.getAccount().getId(),
               newTransaction.getTransactionType(),
               newTransaction.getAmount(),
               newTransaction.getCreatedTime(),
               newTransaction.getAccount().getBalance(),
               newTransaction.getDescription()
       );

    }
    @Transactional
    public TransactionResponse paraCekmeIslemi(Long accountId, BigDecimal amount, String desc) {
        Account account = accountRepository.lockByIdForUpdate(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        account.paraCekme(amount);
        Transaction transaction = Transaction.paraCekme(account, amount, desc);
        Transaction newTransaction =  transactionRepository.save(transaction);
        return new TransactionResponse(
                newTransaction.getId(),
                newTransaction.getAccount().getId(),
                newTransaction.getTransactionType(),
                newTransaction.getAmount(),
                newTransaction.getCreatedTime(),
                newTransaction.getAccount().getBalance(),
                newTransaction.getDescription()
        );

    }
    @Transactional
    public TransferResponse transferIslemi(TransferRequest transferRequest) {

        ThrowExceptionHandler.throwIf(transferRequest.fromAccountId().equals(transferRequest.toAccountId()) ,
                () -> new SameAccountTransferException(transferRequest.toAccountId()));

        Account from = accountRepository.lockByIdForUpdate(transferRequest.fromAccountId()).orElseThrow(() -> new AccountNotFoundException(transferRequest.fromAccountId()));
        Account to = accountRepository.lockByIdForUpdate(transferRequest.toAccountId()).orElseThrow(() -> new AccountNotFoundException(transferRequest.toAccountId()));

        from.paraCekme(transferRequest.amount());
        to.paraYatirma(transferRequest.amount());

        Transaction transactionIn = Transaction.transferGelen(from, transferRequest.amount(), transferRequest.description());
        Transaction transactionOut = Transaction.transferGelen(to, transferRequest.amount(), transferRequest.description());

        transactionRepository.save(transactionIn); transactionRepository.save(transactionOut);

        TransactionResponse trxInResponse = new TransactionResponse(
                transactionIn.getId(),
                transactionIn.getAccount().getId(),
                transactionIn.getTransactionType(),
                transactionIn.getAmount(),
                transactionIn.getCreatedTime(),
                transactionIn.getAccount().getBalance(),
                transactionIn.getDescription()
        );
        TransactionResponse trxOnResponse = new TransactionResponse(
                transactionOut.getId(),
                transactionOut.getAccount().getId(),
                transactionOut.getTransactionType(),
                transactionOut.getAmount(),
                transactionOut.getCreatedTime(),
                transactionOut.getAccount().getBalance(),
                transactionOut.getDescription()
        );

        return new TransferResponse(
                trxInResponse,
                trxOnResponse
        );
    }

    public List<TransactionBrief> last5ForAccount(Long accountId) {
        return transactionRepository.findTop5ByAccount_IdOrderByCreatedTimeDesc(accountId).stream()
                .map( transaction -> new TransactionBrief(
                        transaction.getId(),
                        transaction.getTransactionType(),
                        transaction.getAmount(),
                        transaction.getCreatedTime().toString())
                ).toList();

    }

    public long totalTransactionForAccount(Long accountId) {
        return transactionRepository.countByAccount_Id(accountId);
    }

    public Optional<Transaction> getLastTransactionForAccount(Long accountId) {
            return transactionRepository.findFirstByAccount_IdOrderByCreatedTimeDesc(accountId);
    }


}
