package com.example.creditcardtransactionauthorization.service;

import com.example.creditcardtransactionauthorization.dto.TransactionResponse;
import com.example.creditcardtransactionauthorization.factory.TransactionResponseFactory;
import com.example.creditcardtransactionauthorization.model.Account;
import com.example.creditcardtransactionauthorization.model.Transaction;
import com.example.creditcardtransactionauthorization.repository.AccountRepository;
import com.example.creditcardtransactionauthorization.strategy.BalanceStrategy;
import com.example.creditcardtransactionauthorization.strategy.CashBalanceStrategy;
import com.example.creditcardtransactionauthorization.strategy.FoodBalanceStrategy;
import com.example.creditcardtransactionauthorization.strategy.MealBalanceStrategy;
import com.example.creditcardtransactionauthorization.util.CategoryUtil;
import com.example.creditcardtransactionauthorization.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthorizationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    private final AccountRepository accountRepository;
    private final Map<String, BalanceStrategy> balanceStrategies;

    public AuthorizationService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.balanceStrategies = new HashMap<>();
        this.balanceStrategies.put(Constants.CATEGORY_FOOD, new FoodBalanceStrategy());
        this.balanceStrategies.put(Constants.CATEGORY_MEAL, new MealBalanceStrategy());
        this.balanceStrategies.put(Constants.CATEGORY_CASH, new CashBalanceStrategy());
    }

    @Transactional
    public TransactionResponse authorizeTransaction(Transaction transaction) {
        logger.debug("Authorizing transaction: {}", transaction);
        Account account = accountRepository.findAccountForUpdate(transaction.getAccountId());

        if (account == null) {
            logger.warn("Account not found: {}", transaction.getAccountId());
            return TransactionResponseFactory.createResponse(Constants.CODE_ERROR);
        }

        double amount = transaction.getAmount();
        String category = CategoryUtil.determineCategory(transaction.getMcc(), transaction.getMerchant());
        BalanceStrategy balanceStrategy = balanceStrategies.get(category);

        if (balanceStrategy != null) {
            double balance = balanceStrategy.getBalance(account);
            if (balance >= amount) {
                balanceStrategy.updateBalance(account, balance - amount);
                accountRepository.save(account);
                logger.info("Transaction approved using category: {}", category);
                return TransactionResponseFactory.createResponse(Constants.CODE_APPROVED);
            }
        }

        BalanceStrategy cashStrategy = balanceStrategies.get(Constants.CATEGORY_CASH);
        if (cashStrategy != null) {
            double cashBalance = cashStrategy.getBalance(account);
            if (cashBalance >= amount) {
                cashStrategy.updateBalance(account, cashBalance - amount);
                accountRepository.save(account);
                logger.info("Transaction approved using cash balance. Transaction: {}", transaction);
                return TransactionResponseFactory.createResponse(Constants.CODE_APPROVED);
            }
        }

        logger.warn("Transaction insufficient funds for account: {}", transaction.getAccountId());
        return TransactionResponseFactory.createResponse(Constants.CODE_INSUFFICIENT_FUNDS);
    }
}
