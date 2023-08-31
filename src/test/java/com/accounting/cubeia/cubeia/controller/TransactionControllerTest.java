package com.accounting.cubeia.cubeia.controller;

import com.accounting.cubeia.cubeia.TransactionControllerTestConfig;
import com.accounting.cubeia.cubeia.dto.TransactionDTO;
import com.accounting.cubeia.cubeia.entity.Account;
import com.accounting.cubeia.cubeia.entity.Transaction;
import com.accounting.cubeia.cubeia.repository.IAccountRepository;
import com.accounting.cubeia.cubeia.repository.ITransactionRepository;
import com.accounting.cubeia.cubeia.request.TransferFunds;
import com.accounting.cubeia.cubeia.response.GetTransactionsResponse;
import com.accounting.cubeia.cubeia.response.TransferFundsResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(TransactionController.class)
@Import(TransactionControllerTestConfig.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAccountRepository accountRepository;

    @MockBean
    private ITransactionRepository transactionRepository;

    @Test
    void transferFunds_whenThereInsufficientFunds() throws Exception {
        var sender = new Account(100.0, "John Doe");
        sender.setId(1L);
        var receiver = new Account(200.0, "Max Mustermann");
        receiver.setId(2L);

        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(sender);
        accounts.add(receiver);

        var transferFunds = new TransferFunds(10000.0, sender.getId(), receiver.getId());
        Mockito.when(accountRepository.findByIdIn(eq(Arrays.asList(receiver.getId(), sender.getId())))).thenReturn(accounts);

        String message = "Sending account with does not have enough on balance to complete transaction";
        var response = new TransferFundsResponse(message, false);
        var json = new Gson().toJson(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(transferFunds)))
                .andExpect(MockMvcResultMatchers.content().json(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    void transferFunds_whenThereIsEnoughFunds() throws Exception {
        var sender = new Account(100.0, "John Doe");
        sender.setId(1L);
        var receiver = new Account(200.0, "Max Mustermann");
        receiver.setId(2L);

        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(sender);
        accounts.add(receiver);

        var transferFunds = new TransferFunds(10.0, sender.getId(), receiver.getId());
        Mockito.when(accountRepository.findByIdIn(eq(Arrays.asList(receiver.getId(), sender.getId())))).thenReturn(accounts);
        var newTransaction = new Transaction(10.0, sender, receiver);
        newTransaction.setId(1L);
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(newTransaction);

        String message = String.format("Successfully transferred %f from account with id: %d to account with id: %d with transaction id: %d", transferFunds.getAmount(), sender.getId(), receiver.getId(), newTransaction.getId());
        var response = new TransferFundsResponse(message, true);
        var json = new Gson().toJson(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(transferFunds)))
                .andExpect(MockMvcResultMatchers.content().json(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    void transferFunds_whenAmountIsNegative() throws Exception {
        var transferFunds = new TransferFunds(-1.0, 1L, 2L);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(transferFunds)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\n" +
                        "    \"amount\": \"Amount needs to be positive\"\n" +
                        "}"));

    }

    @Test
    void transferFunds_whenFromAccountIsMissing() throws Exception {
        var transferFunds = new TransferFunds(-1.0, null, 2L);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(transferFunds)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\n" +
                        "    \"fromAccountId\": \"From account id is mandatory\"\n" +
                        "}"));

    }

    @Test
    void getTransactions__accountNotFound() throws Exception {
        when(accountRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());
        var wrongId = 1L;
        var url = String.format("/api/transactions/%d", wrongId);

        var message = String.format("Account with id: %d is not present", wrongId);
        var response = new GetTransactionsResponse(new ArrayList<>(), message);
        var json = new Gson().toJson(response);

        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(json));
    }

    @Test
    void getTransactions__accountIsPresent() throws Exception {
        var accountId = 1L;
        var account1 = new Account(100.0, "John Doe");
        account1.setId(accountId);
        var account2 = new Account(200.0, "Max Mustermann");
        account2.setId(2L);

        var transaction1 = new Transaction(11.0, account1, account2);
        var transaction2 = new Transaction(33.4, account2, account1);
        account1.getOutgoingTransactions().add(transaction1);
        account1.getIncomingTransactions().add(transaction2);

        account2.getIncomingTransactions().add(transaction1);
        account2.getOutgoingTransactions().add(transaction2);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account1));
        var url = String.format("/api/transactions/%d", accountId);
        var message = String.format("Successfully retrieved transactions for account with id: %d", accountId);
        var transactions = new ArrayList<TransactionDTO>();
        transactions.add(new TransactionDTO(transaction1));
        transactions.add(new TransactionDTO(transaction2));

        var response = new GetTransactionsResponse(transactions, message);

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        var actualResponse = mvcResult.getResponse().getContentAsString();
        var actualGetTransactionsResponse = new Gson().fromJson(actualResponse, GetTransactionsResponse.class);

        assertEquals(response.getMessage(), actualGetTransactionsResponse.getMessage());
        assertEquals(response.getSize(), actualGetTransactionsResponse.getSize());
        assertIterableEquals(response.getTransactions(), actualGetTransactionsResponse.getTransactions());

    }

}