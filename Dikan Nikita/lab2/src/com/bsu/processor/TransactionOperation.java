package com.bsu.processor;

import com.bsu.model.Transaction;

public interface TransactionOperation {
    void execute (Transaction transaction);
}
