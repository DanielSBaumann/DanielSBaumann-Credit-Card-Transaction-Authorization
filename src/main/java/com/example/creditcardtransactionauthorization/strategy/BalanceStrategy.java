package com.example.creditcardtransactionauthorization.strategy;

import com.example.creditcardtransactionauthorization.model.Account;

public interface BalanceStrategy {
    double getBalance(Account account);
    void updateBalance(Account account, double newBalance);
}
