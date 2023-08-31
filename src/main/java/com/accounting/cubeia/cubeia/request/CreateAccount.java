package com.accounting.cubeia.cubeia.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateAccount {
    @NotNull(message = "Starting balance is mandatory")
    @Positive(message = "Starting balance needs to be positive")
    Double startingBalance;

    @NotBlank(message = "Account holder is mandatory")
    String accountHolder;

    public CreateAccount(Double startingBalance, String accountHolder) {
        this.startingBalance = startingBalance;
        this.accountHolder = accountHolder;
    }

    public Double getStartingBalance() {
        return startingBalance;
    }

    public void setStartingBalance(Double startingBalance) {
        this.startingBalance = startingBalance;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }
}
