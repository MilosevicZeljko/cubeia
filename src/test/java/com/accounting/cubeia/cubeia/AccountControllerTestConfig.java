package com.accounting.cubeia.cubeia;

import com.accounting.cubeia.cubeia.repository.IAccountRepository;
import com.accounting.cubeia.cubeia.service.impl.AccountService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AccountControllerTestConfig {

    @Bean
    public AccountService accountService(IAccountRepository accountRepository) {
        return new AccountService(accountRepository);
    }
}
