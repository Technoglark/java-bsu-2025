package com.bsu.processor;

import com.bsu.model.Transaction;
import com.bsu.service.interfaces.TransactionService;

public class ProcessTransactionCommand implements Runnable{

    private final Transaction transaction;
    private final TransactionService transactionService;

    public ProcessTransactionCommand(Transaction transaction, TransactionService transactionService) {
        this.transaction = transaction;
        this.transactionService = transactionService;
    }

    @Override
    public void run() {
        transactionService.processTransaction(transaction);
    }
}
