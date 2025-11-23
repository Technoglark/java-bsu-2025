package com.bsu.processor;

import com.bsu.model.Account;
import com.bsu.model.Transaction;
import com.bsu.service.interfaces.AccountService;

public class FreezeOperation implements TransactionOperation {
    private final AccountService accountService;

    public FreezeOperation(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute(Transaction transaction) {
        Account account = transaction.getFromAccount();
        if (account.isFrozen()) {
            System.out.println("Выполнение РАЗМОРОЗКИ счета " + account.getId());
            accountService.unfreezeAccount(account);
        } else {
            System.out.println("Выполнение ЗАМОРОЗКИ счета " + account.getId());
            accountService.freezeAccount(account);
        }
    }
}