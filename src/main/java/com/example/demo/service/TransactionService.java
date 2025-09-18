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

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public TransactionResponse paraYatirmaIslemi(Long accountId, BigDecimal amount, String desc) {
        Account account = getAccountForUpdate(accountId);
        account.paraYatirma(amount);
        Transaction transaction = Transaction.paraYatirma(account, amount, desc);

       return  buildTransactionResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse paraCekmeIslemi(Long accountId, BigDecimal amount, String desc) {
        Account account = getAccountForUpdate(accountId);
        account.paraCekme(amount);
        Transaction transaction = Transaction.paraCekme(account, amount, desc);

        return buildTransactionResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransferResponse transferIslemi(TransferRequest transferRequest) {

        ThrowExceptionHandler.throwIf(transferRequest.fromAccountId().equals(transferRequest.toAccountId()) ,
                () -> new SameAccountTransferException(transferRequest.toAccountId()));

        Account from = getAccountForUpdate(transferRequest.fromAccountId());
        Account to = getAccountForUpdate(transferRequest.toAccountId());

        from.paraCekme(transferRequest.amount());
        to.paraYatirma(transferRequest.amount());

        Transaction transactionIn = Transaction.transferGelen(from, transferRequest.amount(), transferRequest.description());
        Transaction transactionOut = Transaction.transferGelen(to, transferRequest.amount(), transferRequest.description());

        transactionRepository.save(transactionIn);
        transactionRepository.save(transactionOut);

        return new TransferResponse(
                buildTransactionResponse(transactionOut),
                buildTransactionResponse(transactionIn)
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

    private Account getAccountForUpdate(Long accountId) {
        return accountRepository.lockByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private TransactionResponse buildTransactionResponse(Transaction trx) {
        return new TransactionResponse(
                trx.getId(),
                trx.getAccount().getId(),
                trx.getTransactionType(),
                trx.getAmount(),
                trx.getCreatedTime(),
                trx.getAccount().getBalance(),
                trx.getDescription()
        );
    }
}
