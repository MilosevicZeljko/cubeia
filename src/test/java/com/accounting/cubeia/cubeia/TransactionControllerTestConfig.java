package com.accounting.cubeia.cubeia;

import com.accounting.cubeia.cubeia.repository.IAccountRepository;
import com.accounting.cubeia.cubeia.repository.ITransactionRepository;
import com.accounting.cubeia.cubeia.service.impl.TransactionService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TransactionControllerTestConfig {

    @Bean
    public TransactionService transactionService(IAccountRepository accountRepository, ITransactionRepository transactionRepository) {
        return new TransactionService(accountRepository, transactionRepository);
    }

}
