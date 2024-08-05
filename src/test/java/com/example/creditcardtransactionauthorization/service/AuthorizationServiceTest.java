package com.example.creditcardtransactionauthorization.service;

import com.example.creditcardtransactionauthorization.dto.TransactionResponse;
import com.example.creditcardtransactionauthorization.model.Account;
import com.example.creditcardtransactionauthorization.model.Transaction;
import com.example.creditcardtransactionauthorization.repository.AccountRepository;
import com.example.creditcardtransactionauthorization.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthorizationServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    private static final String ACCOUNT_ID = "123";
    private static final double AMOUNT = 50.0;
    private static final String MCC_FOOD = Constants.MCC_FOOD_1;
    private static final String MERCHANT_DEFAULT = "Some Merchant";
    private static final String MERCHANT_UBER_EATS = "UBER EATS SAO PAULO BR";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthorizeTransaction_Approved() {
        Transaction transaction = createTransaction(ACCOUNT_ID, AMOUNT, MCC_FOOD, MERCHANT_DEFAULT);

        Account account = createAccount(ACCOUNT_ID, 100.0, 0.0, 0.0);

        when(accountRepository.findAccountForUpdate(anyString())).thenReturn(account);

        TransactionResponse response = authorizationService.authorizeTransaction(transaction);

        assertEquals(Constants.CODE_APPROVED, response.getCode());
        verify(accountRepository, times(1)).save(account);
        assertEquals(50.0, account.getFoodBalance());
    }

    @Test
    void testAuthorizeTransaction_InsufficientFunds_UseCashBalance() {
        Transaction transaction = createTransaction(ACCOUNT_ID, AMOUNT, MCC_FOOD, MERCHANT_DEFAULT);

        Account account = createAccount(ACCOUNT_ID, 20.0, 0.0, 100.0);

        when(accountRepository.findAccountForUpdate(anyString())).thenReturn(account);

        TransactionResponse response = authorizationService.authorizeTransaction(transaction);

        assertEquals(Constants.CODE_APPROVED, response.getCode());
        verify(accountRepository, times(1)).save(account);
        assertEquals(20.0, account.getFoodBalance());
        assertEquals(50.0, account.getCashBalance());
    }

    @Test
    void testAuthorizeTransaction_InsufficientFunds_Reject() {
        Transaction transaction = createTransaction(ACCOUNT_ID, 150.0, MCC_FOOD, MERCHANT_DEFAULT);

        Account account = createAccount(ACCOUNT_ID, 100.0, 0.0, 20.0);

        when(accountRepository.findAccountForUpdate(anyString())).thenReturn(account);

        TransactionResponse response = authorizationService.authorizeTransaction(transaction);

        assertEquals(Constants.CODE_INSUFFICIENT_FUNDS, response.getCode());
        verify(accountRepository, times(0)).save(account);
    }

    @Test
    void testAuthorizeTransaction_MerchantPrecedence() {
        Transaction transaction = createTransaction(ACCOUNT_ID, AMOUNT, MCC_FOOD, MERCHANT_UBER_EATS);

        Account account = createAccount(ACCOUNT_ID, 0.0, 100.0, 0.0);

        when(accountRepository.findAccountForUpdate(anyString())).thenReturn(account);

        TransactionResponse response = authorizationService.authorizeTransaction(transaction);

        assertEquals(Constants.CODE_APPROVED, response.getCode());
        verify(accountRepository, times(1)).save(account);
        assertEquals(50.0, account.getMealBalance());
    }

    @Test
    void testAuthorizeTransaction_Error() {
        Transaction transaction = createTransaction(ACCOUNT_ID, AMOUNT, MCC_FOOD, MERCHANT_DEFAULT);

        when(accountRepository.findAccountForUpdate(anyString())).thenReturn(null);

        TransactionResponse response = authorizationService.authorizeTransaction(transaction);

        assertEquals(Constants.CODE_ERROR, response.getCode());
        verify(accountRepository, times(0)).save(any(Account.class));
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
