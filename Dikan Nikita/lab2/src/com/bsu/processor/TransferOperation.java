package com.bsu.processor;
import com.bsu.model.Account;
import com.bsu.model.Transaction;


public class TransferOperation implements TransactionOperation{

    @Override
    public void execute(Transaction transaction) {
        Account from = transaction.getFromAccount();
        Account to = transaction.getToAccount();
        if (from.isFrozen()) {
            System.err.println("Ошибка: Счет " + from.getId() + " заморожен. Перевод невозможен.");
            return;
        }
        if (to.isFrozen()) {
            System.err.println("Ошибка: Счет " + to.getId() + " заморожен. Перевод невозможен.");
            return;
        }

        if (from.withdraw(transaction.getAmount())){
            to.deposit(transaction.getAmount());
            System.out.println("Успешный перевод суммы " + transaction.getAmount() + " c аккаунта " + from.getId() + " на аккаунт " + to.getId());
        } else{
            System.err.println("На аккаунте " + from.getId() + " недостаточно средств для перевода");
        }
    }
}
