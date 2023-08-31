package com.accounting.cubeia.cubeia.service;

import com.accounting.cubeia.cubeia.request.TransferFunds;
import com.accounting.cubeia.cubeia.response.GetTransactionsResponse;
import com.accounting.cubeia.cubeia.response.TransferFundsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ITransactionService {
    ResponseEntity<GetTransactionsResponse> getTransactions(Long accountId);

    ResponseEntity<TransferFundsResponse> transferFunds(TransferFunds transferFunds);
}
