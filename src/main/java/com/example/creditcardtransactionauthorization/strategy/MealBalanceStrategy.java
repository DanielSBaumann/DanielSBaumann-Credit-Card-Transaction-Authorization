package com.example.creditcardtransactionauthorization.strategy;

import com.example.creditcardtransactionauthorization.model.Account;

public class MealBalanceStrategy implements BalanceStrategy {

    @Override
    public double getBalance(Account account) {
        return account.getMealBalance();
    }

    @Override
    public void updateBalance(Account account, double newBalance) {
        account.setMealBalance(newBalance);
    }
}
