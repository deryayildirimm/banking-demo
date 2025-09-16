package com.example.demo.model;

import com.example.demo.exception.AccountClosedException;
import com.example.demo.exception.BalanceNotZeroException;
import com.example.demo.exception.NotEnoughBalanceException;
import com.example.demo.exception.ThrowExceptionHandler;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

import java.util.Set;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private BigDecimal balance = BigDecimal.ZERO;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.OPEN;

    private Instant closedAt;
    private String closedReason;

    @OneToMany(mappedBy = "account")
    private Set<Transaction> transactions;

    @Version
    private Long version;


    protected Account() {}

    private Account(BigDecimal balance, Customer customer) {

        this.balance = balance;
        this.customer = customer;

    }

    public static Account open (BigDecimal balance, Customer customer) {
        requirePositive(balance);
        return new Account(balance, customer);
    }

  @PrePersist
    public void prePersist() {

        if(createdAt == null) {
            createdAt = Instant.now();
        }

  }
    // --- Domain davranışları ---

    public void paraYatirma(BigDecimal amount) {
        requirePositive(amount);
        requireOpen();
        balance = balance.add(amount);
    }

    public void paraCekme(BigDecimal amount) {
        requirePositive(amount);
        requireOpen();
        ThrowExceptionHandler.throwIf(balance.compareTo(amount) < 0 ,
                NotEnoughBalanceException::new);

        balance = balance.subtract(amount);
    }

    public void close () {

        

        if(status == AccountStatus.CLOSED) throw new AccountClosedException();
        if(balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new BalanceNotZeroException(balance);
        }

        status = AccountStatus.CLOSED;
        closedAt = Instant.now();
        closedReason = "Closed by" + customer.getName() + "at " + Instant.now();
    }

    private void requireOpen() {
        ThrowExceptionHandler.throwIf(status != AccountStatus.OPEN ,
                AccountClosedException::new);

    }
    private static void requirePositive(BigDecimal a) {

        ThrowExceptionHandler.throwIf((a == null || a.signum() <= 0) ,
                NotEnoughBalanceException::new);

    }

    public Long getId() {return id;}
    public BigDecimal getBalance() {return balance;}
    public Instant getCreatedAt() {return createdAt;}
    public Customer getCustomer() {return customer;}
    public AccountStatus getStatus() {return status;}
    public Set<Transaction> getTransactions() {return transactions;}


}
