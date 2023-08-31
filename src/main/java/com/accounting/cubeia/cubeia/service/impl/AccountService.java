package com.accounting.cubeia.cubeia.service.impl;

import com.accounting.cubeia.cubeia.entity.Account;
import com.accounting.cubeia.cubeia.repository.IAccountRepository;
import com.accounting.cubeia.cubeia.request.CreateAccount;
import com.accounting.cubeia.cubeia.response.CreateAccountResponse;
import com.accounting.cubeia.cubeia.response.GetBalanceResponse;
import com.accounting.cubeia.cubeia.service.IAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountService implements IAccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private IAccountRepository accountRepository;

    @Autowired
    public AccountService(IAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public ResponseEntity<CreateAccountResponse> createAccount(final CreateAccount createAccount) {
        logger.debug("Starting creation of new account");
        Account newAccount = new Account(createAccount);

        logger.debug("Creating account with data: {}", createAccount);

        Account saved = accountRepository.save(newAccount);
        String message = String.format("Successfully created account with id: %d with starting amount: %f", saved.getId(), saved.getBalance());
        logger.debug(message);
        return new ResponseEntity<>(new CreateAccountResponse(message), HttpStatus.CREATED);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<GetBalanceResponse> getBalance(final Long accountId) {
        logger.debug("retrieving account with account id: {}", accountId);
        Optional<Account> optionalAccount = accountRepository.findById(accountId);

        if (optionalAccount.isEmpty()) {
            String message = String.format("Account with id: %d is not present", accountId);
            logger.debug(message);
            return new ResponseEntity<>(new GetBalanceResponse(message, -1.0, false), HttpStatus.NOT_FOUND);
        }

        Double balance = optionalAccount.get().getBalance();
        String message = String.format("Account with id: %d has balance of: %f", accountId, balance);
        logger.debug(message);
        return new ResponseEntity<>(new GetBalanceResponse(message, balance, true), HttpStatus.OK);
    }
}
