package com.example.creditcardtransactionauthorization.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Account {
    @Id
    private String id;
    private double foodBalance;
    private double mealBalance;
    private double cashBalance;
    @Version
    private Long version;
}