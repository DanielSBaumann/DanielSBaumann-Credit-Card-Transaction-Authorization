package com.example.creditcardtransactionauthorization.factory;

import com.example.creditcardtransactionauthorization.dto.TransactionResponse;

public class TransactionResponseFactory {
    public static TransactionResponse createResponse(String code) {
        return new TransactionResponse(code);
    }
}

