package com.accounting.cubeia.cubeia.service.impl;

import com.accounting.cubeia.cubeia.dto.TransactionDTO;
import com.accounting.cubeia.cubeia.entity.Account;
import com.accounting.cubeia.cubeia.entity.Transaction;
import com.accounting.cubeia.cubeia.repository.IAccountRepository;
import com.accounting.cubeia.cubeia.repository.ITransactionRepository;
import com.accounting.cubeia.cubeia.request.TransferFunds;
import com.accounting.cubeia.cubeia.response.GetTransactionsResponse;
import com.accounting.cubeia.cubeia.response.TransferFundsResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class TransactionServiceTest {

    @Mock
    private IAccountRepository accountRepository;

    @Mock
    private ITransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    public void testTransferFunds_accountNotFound() {
        Mockito.when(accountRepository.findByIdIn(anyList())).thenReturn(Collections.emptyList());
        var transferFunds = new TransferFunds(100.0, 1L, 2L);

        ResponseEntity<TransferFundsResponse> response = transactionService.transferFunds(transferFunds);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Cant find accounts", response.getBody().getMessage());

    }

    @Test
    public void testTransferFunds_insufficientFunds() {
        var sender = new Account(100.0, "John Doe");
        sender.setId(1L);
        var receiver = new Account(100.0, "Max Mustermann");
        receiver.setId(2L);

        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(sender);
        accounts.add(receiver);

        var transferFunds = new TransferFunds(10000.0, sender.getId(), receiver.getId());
        Mockito.when(accountRepository.findByIdIn(eq(Arrays.asList(receiver.getId(), sender.getId())))).thenReturn(accounts);
        ResponseEntity<TransferFundsResponse> response = transactionService.transferFunds(transferFunds);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Sending account with does not have enough on balance to complete transaction", response.getBody().getMessage());
    }

    @Test
    public void testTransferFunds_sufficientFunds() {
        var sender = new Account(100.0, "John Doe");
        sender.setId(1L);
        var receiver = new Account(100.0, "Max Mustermann");
        receiver.setId(2L);

        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(sender);
        accounts.add(receiver);

        var transferFunds = new TransferFunds(10.0, sender.getId(), receiver.getId());
        Mockito.when(accountRepository.findByIdIn(eq(Arrays.asList(receiver.getId(), sender.getId())))).thenReturn(accounts);
        var newTransaction = new Transaction(10.0, sender, receiver);
        newTransaction.setId(1L);

        // Mockito any because i cant know the timestamp
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(newTransaction);
        ResponseEntity<TransferFundsResponse> response = transactionService.transferFunds(transferFunds);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
        assertEquals(String.format("Successfully transferred %f from account with id: %d to account with id: %d with transaction id: %d", transferFunds.getAmount(), sender.getId(), receiver.getId(), 1L), response.getBody().getMessage());
    }

    @Test
    public void getTransactions_forValidAccountId() {
        var user1 = new Account(100.0, "John Doe");
        user1.setId(1L);
        var user2 = new Account(100.0, "Max Mustermann");
        user2.setId(2L);
        var user3 = new Account(100.0, "Juan Perez");
        user3.setId(2L);

        var transaction1 = new Transaction(1.1, user1, user2);
        var transaction2 = new Transaction(2.0, user2, user1);
        var transaction3 = new Transaction(5.0, user2, user3);

        user1.getOutgoingTransactions().add(transaction1);
        user1.getIncomingTransactions().add(transaction2);

        user2.getIncomingTransactions().add(transaction1);
        user2.getOutgoingTransactions().add(transaction2);
        user2.getOutgoingTransactions().add(transaction3);

        user3.getIncomingTransactions().add(transaction3);

        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(accountRepository.findById(2L)).thenReturn(Optional.of(user2));
        Mockito.when(accountRepository.findById(3L)).thenReturn(Optional.of(user3));

        ResponseEntity<GetTransactionsResponse> response = transactionService.getTransactions(user1.getId());

        var message = String.format("Successfully retrieved transactions for account with id: %d", user1.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody().getMessage());
        assertEquals(2, response.getBody().getSize());
        assertEquals(2, response.getBody().getTransactions().size());

        assertTrue(response.getBody().getTransactions().stream().anyMatch(transaction -> transaction.equals(new TransactionDTO(transaction1))));
        assertTrue(response.getBody().getTransactions().stream().anyMatch(transaction -> transaction.equals(new TransactionDTO(transaction2))));

    }
}