package com.accounting.cubeia.cubeia.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TransferFunds {

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount needs to be positive")
    Double amount;

    @NotNull(message = "From account id is mandatory")
    Long fromAccountId;

    @NotNull(message = "To account id is mandatory")
    Long toAccountId;

    public TransferFunds(Double amount, Long fromAccountId, Long toAccountId) {
        this.amount = amount;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
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
}
