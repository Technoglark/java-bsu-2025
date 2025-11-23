package com.bsu.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final LocalDateTime timestamp;
    private final Actions type;
    private final BigDecimal amount;
    private final Account fromAccount;
    private final Account toAccount;

    public Transaction(Actions type, BigDecimal amount, Account fromAccount) {
        this.id = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.fromAccount = fromAccount;
        this.toAccount = null;
    }

    public Transaction(BigDecimal amount, Account fromAccount, Account toAccount) {
        this.id = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.type = Actions.TRANSFER;
        this.amount = amount;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
    }

    public UUID getId(){
        return id;
    }
    public LocalDateTime getTimestamp(){
        return timestamp;
    }
    public Actions getType(){
        return type;
    }
    public BigDecimal getAmount(){
        return amount;
    }
    public Account getFromAccount(){
        return fromAccount;
    }
    public Account getToAccount(){
        return toAccount;
    }

}

