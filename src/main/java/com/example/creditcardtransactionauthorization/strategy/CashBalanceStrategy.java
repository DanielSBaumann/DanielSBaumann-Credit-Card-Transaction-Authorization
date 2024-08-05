package com.example.creditcardtransactionauthorization.strategy;

import com.example.creditcardtransactionauthorization.model.Account;

public class CashBalanceStrategy implements BalanceStrategy {

    @Override
    public double getBalance(Account account) {
        return account.getCashBalance();
    }

    @Override
    public void updateBalance(Account account, double newBalance) {
        account.setCashBalance(newBalance);
    }
}
