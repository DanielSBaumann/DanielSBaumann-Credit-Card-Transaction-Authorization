package com.example.creditcardtransactionauthorization.strategy;

import com.example.creditcardtransactionauthorization.model.Account;

public class FoodBalanceStrategy implements BalanceStrategy {

    @Override
    public double getBalance(Account account) {
        return account.getFoodBalance();
    }

    @Override
    public void updateBalance(Account account, double newBalance) {
        account.setFoodBalance(newBalance);
    }
}
