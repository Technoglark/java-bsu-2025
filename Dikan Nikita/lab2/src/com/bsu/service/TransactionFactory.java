package com.bsu.service;
import com.bsu.model.Account;
import com.bsu.model.Actions;
import com.bsu.model.Transaction;
import java.math.BigDecimal;

public class TransactionFactory {

    public static Transaction createRefillTransaction(Account account, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной.");
        }
        return new Transaction(Actions.REFILL, amount, account);
    }

    public static Transaction createWithdrawTransaction(Account account, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма снятия должна быть положительной.");
        }
        return new Transaction(Actions.WITHDRAWING, amount, account);
    }

    public static Transaction createTransferTransaction(Account from, Account to, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть положительной.");
        }
        if (from == null || to == null) {
            throw new IllegalArgumentException("Счета отправителя и получателя не могут быть null.");
        }
        return new Transaction(amount, from, to);
    }

    public static Transaction createFreezeTransaction(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Счет для заморозки не может быть null.");
        }
        return new Transaction(Actions.FREEZING, BigDecimal.ZERO, account);
    }
}
