package com.example.demo.model;

import com.example.demo.exception.AmountNotEnoughError;
import com.example.demo.exception.ThrowExceptionHandler;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;
    private Instant createdTime ;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @NonNull
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    protected Transaction() {}


    private Transaction(Account acc, TransactionType type, BigDecimal amount, String desc) {
        this.account = acc; this.transactionType = type; this.amount = amount; this.description = desc;
    }

    // ---- Static Factory'ler ----

    public static Transaction paraYatirma(Account acc, BigDecimal amount, String desc) {
        requirePositive(amount);
        return new Transaction(acc, TransactionType.DEPOSIT, amount, desc);
    }
    public static Transaction paraCekme(Account acc, BigDecimal amount, String desc) {
        requirePositive(amount);
        return new Transaction(acc, TransactionType.WITHDRAW, amount, desc);
    }
    public static Transaction transferGelen(Account acc, BigDecimal amount, String desc) {
        requirePositive(amount);
        return new Transaction(acc, TransactionType.TRANSFER_IN, amount, desc);
    }

    public static Transaction transferGiden(Account acc, BigDecimal amount, String desc) {
        requirePositive(amount);
        return new Transaction(acc, TransactionType.TRANSFER_OUT, amount, desc);
    }


    private static void requirePositive(BigDecimal a) {
        ThrowExceptionHandler.throwIf((a == null || a.signum() <= 0),
                AmountNotEnoughError::new);

    }

    @PrePersist
    public void prePersist() {
        createdTime = Instant.now();
    }

    public Long getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }
    public Instant getCreatedTime() {
        return createdTime;
    }
    public TransactionType getTransactionType() {
        return transactionType;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public Account getAccount() {
        return account;
    }



}
