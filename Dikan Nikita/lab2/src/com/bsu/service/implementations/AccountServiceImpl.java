package com.bsu.service.implementations;

import com.bsu.model.Account;
import com.bsu.service.interfaces.AccountService;
import com.bsu.service.interfaces.LoggingService;

public class AccountServiceImpl implements AccountService {
    private final LoggingService logger;

    public AccountServiceImpl(LoggingService logger) {
        this.logger = logger;
    }

    @Override
    public void freezeAccount(Account account) {
        if (!account.isFrozen()) {
            account.setFrozen(true);
            logger.log("Счет " + account.getId() + " был заморожен.");
        } else {
            logger.log("Счет " + account.getId() + " уже был заморожен. Действие пропущено.");
        }
    }

    @Override
    public void unfreezeAccount(Account account) {
        if (account.isFrozen()) {
            account.setFrozen(false);
            logger.log("Счет " + account.getId() + " был разморожен.");
        } else {
            logger.log("Счет " + account.getId() + " не был заморожен. Действие пропущено.");
        }
    }
}
