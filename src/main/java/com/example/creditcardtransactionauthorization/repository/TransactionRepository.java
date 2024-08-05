package com.example.creditcardtransactionauthorization.repository;

import com.example.creditcardtransactionauthorization.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {}
