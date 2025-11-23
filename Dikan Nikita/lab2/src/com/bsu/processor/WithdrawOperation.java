package com.bsu.processor;

import com.bsu.model.Account;
import com.bsu.model.Transaction;

public class WithdrawOperation implements TransactionOperation{

    @Override
    public void execute(Transaction transaction) {
        Account account = transaction.getFromAccount();
        if (account.isFrozen()) {
            System.err.println("Ошибка: Счет " + account.getId() + " заморожен. Снятие невозможно.");
            return;
        }
        if (account.withdraw(transaction.getAmount())) {
            System.out.println("С аккаунта " + account.getId() + " успешно снята сумма " + transaction.getAmount());
        } else {
            System.err.println("На аккаунте " + account.getId() + " недостаточно средств для снятия");
        }
    }
}
