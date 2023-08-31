package com.accounting.cubeia.cubeia.service.impl;

import com.accounting.cubeia.cubeia.entity.Account;
import com.accounting.cubeia.cubeia.repository.IAccountRepository;
import com.accounting.cubeia.cubeia.request.CreateAccount;
import com.accounting.cubeia.cubeia.response.CreateAccountResponse;
import com.accounting.cubeia.cubeia.response.GetBalanceResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class AccountServiceTest {

    @Mock
    private IAccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    public void testCreateAccount() {
        var balance = 100.0;
        CreateAccount createAccount = new CreateAccount(balance, "John Doe");

        // Mock the account repository
        Account account = new Account();
        account.setId(1L);
        account.setBalance(balance);
        Mockito.when(accountRepository.save(Mockito.any())).thenReturn(account);

        // Call the createAccount method
        ResponseEntity<CreateAccountResponse> response = accountService.createAccount(createAccount);

        // Assert that the response is successful
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(String.format("Successfully created account with id: 1 with starting amount: %f", balance), response.getBody().getMessage());
    }

    @Test
    public void testGetBalanceWithInvalidAccountId() {
        // Create a mock account repository that returns an empty optional
        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the getBalance method
        ResponseEntity<GetBalanceResponse> response = accountService.getBalance(1L);

        // Assert that the response is not found
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account with id: 1 is not present", response.getBody().getMessage());
        assertFalse(response.getBody().getSuccess());
    }

    @Test
    public void testGetBalanceWithValidAccountId() {
        var balance = 100.0;
        Account account = new Account(balance, "John Doe");
        account.setId(1L);

        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        ResponseEntity<GetBalanceResponse> response = accountService.getBalance(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(String.format("Account with id: 1 has balance of: %f", balance), response.getBody().getMessage());
        assertTrue(response.getBody().getSuccess());
        assertEquals(balance, response.getBody().getAmount());
    }
}