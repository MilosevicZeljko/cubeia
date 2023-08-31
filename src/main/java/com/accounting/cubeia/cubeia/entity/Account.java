package com.accounting.cubeia.cubeia.entity;

import com.accounting.cubeia.cubeia.request.CreateAccount;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double balance;

    @Column(nullable = false)
    private String accountHolderName;

    @OneToMany(mappedBy = "fromAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Transaction> outgoingTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "toAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Transaction> incomingTransactions = new ArrayList<>();

    public Account() {
    }

    public Account(Double balance, String accountHolderName) {
        this.balance = balance;
        this.accountHolderName = accountHolderName;
    }

    public Account(CreateAccount createAccount) {
        this(createAccount.getStartingBalance(), createAccount.getAccountHolder());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public List<Transaction> getOutgoingTransactions() {
        return outgoingTransactions;
    }

    public List<Transaction> getIncomingTransactions() {
        return incomingTransactions;
    }
}
