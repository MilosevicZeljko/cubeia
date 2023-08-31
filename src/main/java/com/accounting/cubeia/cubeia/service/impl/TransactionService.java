package com.accounting.cubeia.cubeia.service.impl;

import com.accounting.cubeia.cubeia.dto.TransactionDTO;
import com.accounting.cubeia.cubeia.entity.Account;
import com.accounting.cubeia.cubeia.entity.Transaction;
import com.accounting.cubeia.cubeia.repository.IAccountRepository;
import com.accounting.cubeia.cubeia.repository.ITransactionRepository;
import com.accounting.cubeia.cubeia.request.TransferFunds;
import com.accounting.cubeia.cubeia.response.GetTransactionsResponse;
import com.accounting.cubeia.cubeia.response.TransferFundsResponse;
import com.accounting.cubeia.cubeia.service.ITransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class TransactionService implements ITransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private IAccountRepository accountRepository;

    private ITransactionRepository transactionRepository;

    @Autowired
    public TransactionService(IAccountRepository accountRepository, ITransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<GetTransactionsResponse> getTransactions(final Long accountId) {
        logger.info("retrieving transactions for account with id: {}", accountId);
        Optional<Account> optionalAccount = accountRepository.findById(accountId);

        if (optionalAccount.isEmpty()) {
            String message = String.format("Account with id: %d is not present", accountId);
            logger.info(message);
            return new ResponseEntity<>(new GetTransactionsResponse(message), HttpStatus.NOT_FOUND);
        }

        Account account = optionalAccount.get();

        Stream<Transaction> incomingTransactions = account.getIncomingTransactions().stream();
        Stream<Transaction> outgoingTransactions = account.getOutgoingTransactions().stream();

        List<TransactionDTO> merged = Stream.concat(incomingTransactions, outgoingTransactions)
                .sorted(Comparator.comparing(Transaction::getTimestamp))
                .map(TransactionDTO::new)
                .toList();

        logger.info("Found transactions for account with id: {}", accountId);
        logger.info("Transactions: {}", merged);

        String message = String.format("Successfully retrieved transactions for account with id: %d", accountId);
        var response = new GetTransactionsResponse(merged, message);
        logger.info(message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public ResponseEntity<TransferFundsResponse> transferFunds(TransferFunds transferFunds) {
        logger.info("Transferring funds");
        List<Account> accounts = accountRepository.findByIdIn(Arrays.asList(transferFunds.getToAccountId(), transferFunds.getFromAccountId()));

        if (accounts.size() != 2) {
            var message = "Cant find accounts";
            logger.info(message);
            return new ResponseEntity<>(new TransferFundsResponse(message, false), HttpStatus.BAD_REQUEST);
        } else {
            // we have both accounts
            var senderOptional = accounts.stream()
                    .filter(account -> account.getId().equals(transferFunds.getFromAccountId()))
                    .findFirst();
            var receiverOptional = accounts.stream()
                    .filter(account -> account.getId().equals(transferFunds.getToAccountId()))
                    .findFirst();

            if (senderOptional.isEmpty() || receiverOptional.isEmpty()) {
                var message = "Sender or receiver account not found";
                logger.info(message);
                return new ResponseEntity<>(new TransferFundsResponse(message, false), HttpStatus.BAD_REQUEST);
            }

            var sender = senderOptional.get();
            var receiver = receiverOptional.get();

            if (sender.getBalance() >= transferFunds.getAmount()) {
                // we can send money
                return transferMoney(transferFunds, sender, receiver);
            } else {
                // we cant send money
                return handleInsufficientFunds();
            }
        }
    }

    private ResponseEntity<TransferFundsResponse> transferMoney(TransferFunds transferFunds, Account sender, Account receiver) {
        sender.setBalance(sender.getBalance() - transferFunds.getAmount());
        receiver.setBalance(receiver.getBalance() + transferFunds.getAmount());

        Transaction newTransaction = new Transaction(transferFunds.getAmount(), sender, receiver);

        accountRepository.saveAll(Arrays.asList(sender, receiver));
        Transaction savedTransaction = transactionRepository.save(newTransaction);

        String message = String.format("Successfully transferred %f from account with id: %d to account with id: %d with transaction id: %d", transferFunds.getAmount(), sender.getId(), receiver.getId(), savedTransaction.getId());
        logger.info(message);
        return new ResponseEntity<>(new TransferFundsResponse(message, true), HttpStatus.CREATED);
    }

    private ResponseEntity<TransferFundsResponse> handleInsufficientFunds() {
        String message = "Sending account with does not have enough on balance to complete transaction";
        logger.info(message);
        return new ResponseEntity<>(new TransferFundsResponse(message, false), HttpStatus.BAD_REQUEST);
    }
}
