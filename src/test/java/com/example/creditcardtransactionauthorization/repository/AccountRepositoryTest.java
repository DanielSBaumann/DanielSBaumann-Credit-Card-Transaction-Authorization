package com.example.creditcardtransactionauthorization.repository;

import com.example.creditcardtransactionauthorization.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void testSaveAndFindById() {
        Account account = createAccount("123", 100.0, 100.0, 100.0);
        accountRepository.save(account);

        Optional<Account> foundAccount = accountRepository.findById("123");
        assertTrue(foundAccount.isPresent());
        assertEquals(100.0, foundAccount.get().getFoodBalance());
        assertEquals(100.0, foundAccount.get().getMealBalance());
        assertEquals(100.0, foundAccount.get().getCashBalance());
    }

    @Test
    void testUpdateAccount() {
        Account account = createAccount("123", 100.0, 100.0, 100.0);
        accountRepository.save(account);

        Account savedAccount = accountRepository.findById("123").get();
        savedAccount.setFoodBalance(50.0);
        accountRepository.save(savedAccount);

        Account updatedAccount = accountRepository.findById("123").get();
        assertEquals(50.0, updatedAccount.getFoodBalance());
    }

    @Test
    void testDeleteAccount() {
        Account account = createAccount("123", 100.0, 100.0, 100.0);
        accountRepository.save(account);

        accountRepository.deleteById("123");

        Optional<Account> deletedAccount = accountRepository.findById("123");
        assertTrue(deletedAccount.isEmpty());
    }

    private Account createAccount(String accountId, double foodBalance, double mealBalance, double cashBalance) {
        Account account = new Account();
        account.setId(accountId);
        account.setFoodBalance(foodBalance);
        account.setMealBalance(mealBalance);
        account.setCashBalance(cashBalance);
        return account;
    }
}
