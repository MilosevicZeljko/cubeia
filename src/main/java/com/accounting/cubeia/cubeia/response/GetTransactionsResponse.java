package com.accounting.cubeia.cubeia.response;

import com.accounting.cubeia.cubeia.dto.TransactionDTO;
import com.accounting.cubeia.cubeia.entity.Transaction;

import java.util.ArrayList;
import java.util.List;

public class GetTransactionsResponse {
    List<TransactionDTO> transactions;

    String message;

    Integer size;
    public GetTransactionsResponse() {
    }
    public GetTransactionsResponse(String message) {
        this.transactions = new ArrayList<>();
        this.message = message;
        this.size = 0;
    }

    public GetTransactionsResponse(List<TransactionDTO> transactions, String message) {
        this.transactions = transactions;
        this.message = message;
        this.size = transactions.size();
    }

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }



}
