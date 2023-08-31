package com.accounting.cubeia.cubeia.dto;

import com.accounting.cubeia.cubeia.entity.Transaction;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

public class TransactionDTO implements Serializable {
    private Long id;

    private Double amount;

    private Long fromAccountId;

    private Long toAccountId;

    private Timestamp timestamp;

    public TransactionDTO() {
    }

    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.fromAccountId = transaction.getFromAccount().getId();
        this.toAccountId = transaction.getToAccount().getId();
        this.timestamp = transaction.getTimestamp();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionDTO forChecking = (TransactionDTO) o;
        return Objects.equals(amount, forChecking.amount) &&
                Objects.equals(fromAccountId, forChecking.fromAccountId) &&
                Objects.equals(toAccountId, forChecking.toAccountId);
    }
}
