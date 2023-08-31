package com.accounting.cubeia.cubeia.service;

import com.accounting.cubeia.cubeia.request.CreateAccount;
import com.accounting.cubeia.cubeia.response.CreateAccountResponse;
import com.accounting.cubeia.cubeia.response.GetBalanceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface IAccountService {
    ResponseEntity<CreateAccountResponse> createAccount(CreateAccount createAccount);

    ResponseEntity<GetBalanceResponse> getBalance(Long accountId);
}
