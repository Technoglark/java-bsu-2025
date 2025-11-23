package com.bsu.processor;
import com.bsu.model.Account;
import com.bsu.model.Transaction;

import java.math.BigDecimal;

public class RefillOperation implements TransactionOperation{
    @Override
    public void execute(Transaction transaction) {
        Account account = transaction.getFromAccount();
        if (account.isFrozen()){
            System.err.println("Перевод невозможен. Аккаунт заморожен");
            return;
        }
        account.deposit(transaction.getAmount());
        System.out.println("Пополнение счета " + account.getId() + " на " + transaction.getAmount());
    }
}
