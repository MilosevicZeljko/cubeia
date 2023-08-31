package com.accounting.cubeia.cubeia.controller;

import com.accounting.cubeia.cubeia.AccountControllerTestConfig;
import com.accounting.cubeia.cubeia.entity.Account;
import com.accounting.cubeia.cubeia.repository.IAccountRepository;
import com.accounting.cubeia.cubeia.request.CreateAccount;
import com.accounting.cubeia.cubeia.response.CreateAccountResponse;
import com.accounting.cubeia.cubeia.response.GetBalanceResponse;
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

import java.util.Optional;

import static org.mockito.Mockito.when;

@WebMvcTest(AccountController.class)
@Import(AccountControllerTestConfig.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAccountRepository accountRepository;

    @Test
    public void testOpenAccount() throws Exception {
        var account = new Account(100.0, "John Doe");
        account.setId(1L);
        var createAccount = new CreateAccount(100.0, "John Doe");
        var createAccountJson = new Gson().toJson(createAccount);

        when(accountRepository.save(Mockito.any(Account.class))).thenReturn(account);
        var message = String.format("Successfully created account with id: %d with starting amount: %f", account.getId(), account.getBalance());
        var expectedResponse = new CreateAccountResponse(message);
        var expectedJson = new Gson().toJson(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAccountJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));

    }

    @Test
    public void testOpenAccount_withNegativeStartingAmount() throws Exception {
        var createAccount = new CreateAccount(-100.0, "John Doe");
        var createAccountJson = new Gson().toJson(createAccount);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAccountJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\n" +
                        "    \"startingBalance\": \"Starting balance needs to be positive\"\n" +
                        "}"));
    }

    @Test
    public void testOpenAccount_withoutAccountHolder() throws Exception {
        var createAccount = new CreateAccount(100.0, "");
        var createAccountJson = new Gson().toJson(createAccount);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAccountJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\n" +
                        "    \"accountHolder\": \"Account holder is mandatory\"" +
                        "}"));
    }

    @Test
    public void testOpenAccount_withoutAccountHolderAndNegativeStartingAmount() throws Exception {
        var createAccount = new CreateAccount(-100.0, "");
        var createAccountJson = new Gson().toJson(createAccount);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAccountJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\n" +
                        "    \"accountHolder\": \"Account holder is mandatory\",\n" +
                        "    \"startingBalance\": \"Starting balance needs to be positive\"\n" +
                        "}"));
    }

    @Test
    public void getAccountBalance_accountNotFound() throws Exception {
        when(accountRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());
        var wrongId = 1L;
        var url = String.format("/api/accounts/%d", wrongId);
        var message = String.format("Account with id: %d is not present", wrongId);
        var response = new GetBalanceResponse(message, -1.0, false);
        var json = new Gson().toJson(response);

        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(json));
    }

    @Test
    public void getAccountBalance_accountFound() throws Exception {
        var accountId = 1L;
        var balance = 100.0;
        var account = new Account(balance, "John Doe");
        account.setId(accountId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        var url = String.format("/api/accounts/%d", accountId);
        var message = String.format("Account with id: %d has balance of: %f", accountId, balance);
        var response = new GetBalanceResponse(message, balance, true);
        var json = new Gson().toJson(response);

        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(json));
    }

}