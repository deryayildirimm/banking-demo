package com.example.demo.model;

import com.example.demo.common.ErrorMessages;
import com.example.demo.exception.CustomerAlreadyDeletedException;
import com.example.demo.exception.ThrowExceptionHandler;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Set;

@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String address;
    private String phone;
    private String email;

    private boolean deleted = Boolean.FALSE;
    private Instant deletedAt;
    private String deleteReason;

    @OneToMany(mappedBy = "customer" )
    private Set<Account> accountSet;


    protected Customer() {}

    private Customer(String name, String address, String phone, String email) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    public static Customer of(String name, String address, String phone, String email) {
        return new Customer(name, address, phone, email);
    }

    public void softDelete(){
        ThrowExceptionHandler.throwIf(deleted, CustomerAlreadyDeletedException::new);

        deletedAt = Instant.now();
        deleted = true;
        deleteReason = "Deleted" ;
    }

    public Long getId() {return id;}
    public String getName() {return name;}
    public String getAddress() {return address;}
    public String getPhone() {return phone;}
    public String getEmail() {return email;}
    public boolean isDeleted() {return deleted;}
    public Instant getDeletedAt() {return deletedAt;}
    public Set<Account> getAccountSet() {return accountSet;}




}
