package com.accounting.cubeia.cubeia.controller;

import com.accounting.cubeia.cubeia.request.TransferFunds;
import com.accounting.cubeia.cubeia.response.GetTransactionsResponse;
import com.accounting.cubeia.cubeia.response.TransferFundsResponse;
import com.accounting.cubeia.cubeia.service.ITransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private ITransactionService transactionService;

    @Autowired
    public TransactionController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferFundsResponse> transferFunds(@Valid @RequestBody TransferFunds transferFunds) {
        return transactionService.transferFunds(transferFunds);
    }

    @GetMapping("/{accountId}")
    @ResponseBody
    public ResponseEntity<GetTransactionsResponse> getTransactions(@PathVariable("accountId") Long accountId) {
        return this.transactionService.getTransactions(accountId);
    }
}
