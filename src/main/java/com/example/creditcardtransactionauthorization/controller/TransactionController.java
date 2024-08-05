package com.example.creditcardtransactionauthorization.controller;

import com.example.creditcardtransactionauthorization.dto.TransactionResponse;
import com.example.creditcardtransactionauthorization.model.Transaction;
import com.example.creditcardtransactionauthorization.service.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<TransactionResponse> authorizeTransaction(@RequestBody Transaction transaction) {
        logger.debug("Received transaction request: {}", transaction);
        TransactionResponse response = authorizationService.authorizeTransaction(transaction);
        logger.debug("Transaction response: {}", response);
        return ResponseEntity.ok(response);
    }
}
