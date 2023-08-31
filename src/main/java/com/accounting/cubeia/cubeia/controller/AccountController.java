package com.accounting.cubeia.cubeia.controller;

import com.accounting.cubeia.cubeia.request.CreateAccount;
import com.accounting.cubeia.cubeia.response.CreateAccountResponse;
import com.accounting.cubeia.cubeia.response.GetBalanceResponse;
import com.accounting.cubeia.cubeia.service.IAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/accounts")
public class AccountController {

    private IAccountService accountService;

    @Autowired
    public AccountController(IAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(path = "/")
    public ResponseEntity<CreateAccountResponse> openAccount(@Valid @RequestBody CreateAccount openAccount) {
        return accountService.createAccount(openAccount);
    }

    @GetMapping(path = "/{accountId}")
    public ResponseEntity<GetBalanceResponse> getAccountBalance(@PathVariable Long accountId) {
        return accountService.getBalance(accountId);
    }
}
