package com.example.creditcardtransactionauthorization.service;

import com.example.creditcardtransactionauthorization.dto.TransactionResponse;
import com.example.creditcardtransactionauthorization.model.Account;
import com.example.creditcardtransactionauthorization.model.Transaction;
import com.example.creditcardtransactionauthorization.repository.AccountRepository;
import com.example.creditcardtransactionauthorization.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Rollback
class AuthorizationServiceIntegrationTest {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private AccountRepository accountRepository;

    private static final String ACCOUNT_ID = "123";
    private static final double AMOUNT = 50.0;
    private static final String MCC_FOOD = Constants.MCC_FOOD_1;
    private static final String MERCHANT_DEFAULT = "Some Merchant";
    private static final String MERCHANT_UBER_EATS = "UBER EATS SAO PAULO BR";

    private Account account;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();

        account = createAccount(ACCOUNT_ID, 100.0, 100.0, 100.0);
        accountRepository.save(account);
    }

    @Test
    @Transactional
    void testAuthorizeTransaction_Approved() {
        Transaction transaction = createTransaction(ACCOUNT_ID, AMOUNT, MCC_FOOD, MERCHANT_DEFAULT);

        TransactionResponse response = authorizationService.authorizeTransaction(transaction);

        assertEquals(Constants.CODE_APPROVED, response.getCode());
        Account updatedAccount = accountRepository.findById(ACCOUNT_ID).orElse(null);
        assertEquals(50.0, updatedAccount.getFoodBalance());
    }

    @Test
    @Transactional
    void testAuthorizeTransaction_InsufficientFunds_UseCashBalance() {
        account.setFoodBalance(20.0);
        accountRepository.save(account);

        Transaction transaction = createTransaction(ACCOUNT_ID, AMOUNT, MCC_FOOD, MERCHANT_DEFAULT);

        TransactionResponse response = authorizationService.authorizeTransaction(transaction);

        assertEquals(Constants.CODE_APPROVED, response.getCode());
        Account updatedAccount = accountRepository.findById(ACCOUNT_ID).orElse(null);
        assertEquals(20.0, updatedAccount.getFoodBalance());
        assertEquals(50.0, updatedAccount.getCashBalance());
    }

    @Test
    @Transactional
    void testAuthorizeTransaction_InsufficientFunds_Reject() {
        account.setFoodBalance(50.0);
        account.setCashBalance(20.0);
        accountRepository.save(account);

        Transaction transaction = createTransaction(ACCOUNT_ID, 150.0, MCC_FOOD, MERCHANT_DEFAULT);

        TransactionResponse response = authorizationService.authorizeTransaction(transaction);

        assertEquals(Constants.CODE_INSUFFICIENT_FUNDS, response.getCode());
        Account updatedAccount = accountRepository.findById(ACCOUNT_ID).orElse(null);
        assertEquals(50.0, updatedAccount.getFoodBalance());
        assertEquals(20.0, updatedAccount.getCashBalance());
    }

    @Test
    @Transactional
    void testAuthorizeTransaction_MerchantPrecedence() {
        Transaction transaction = createTransaction(ACCOUNT_ID, AMOUNT, MCC_FOOD, MERCHANT_UBER_EATS);

        TransactionResponse response = authorizationService.authorizeTransaction(transaction);

        assertEquals(Constants.CODE_APPROVED, response.getCode());
        Account updatedAccount = accountRepository.findById(ACCOUNT_ID).orElse(null);
        assertEquals(50.0, updatedAccount.getMealBalance());
    }

    private Transaction createTransaction(String accountId, double amount, String mcc, String merchant) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setMcc(mcc);
        transaction.setMerchant(merchant);
        return transaction;
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

