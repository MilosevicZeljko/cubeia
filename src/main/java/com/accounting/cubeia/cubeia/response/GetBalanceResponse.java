package com.accounting.cubeia.cubeia.response;

public class GetBalanceResponse {


    String message;

    Double amount;

    Boolean success;

    public GetBalanceResponse(String message, Double amount, Boolean success) {
        this.message = message;
        this.amount = amount;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
