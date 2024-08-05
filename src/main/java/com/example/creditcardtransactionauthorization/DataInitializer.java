package com.example.creditcardtransactionauthorization;

import com.example.creditcardtransactionauthorization.model.Account;
import com.example.creditcardtransactionauthorization.model.Transaction;
import com.example.creditcardtransactionauthorization.repository.AccountRepository;
import com.example.creditcardtransactionauthorization.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        return args -> {
            if (!accountRepository.existsById("123")) {
                Account account = new Account();
                account.setId("123");
                account.setFoodBalance(100.0);
                account.setMealBalance(100.0);
                account.setCashBalance(100.0);
                accountRepository.save(account);
                logger.info("Inserted initial account: {}", account);
            } else {
                logger.info("Account with ID 123 already exists");
            }
        };
    }
}