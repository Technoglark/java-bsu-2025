package com.bsu.service.observer;

import com.bsu.model.Account;

public class NotificationService implements EventListener {
    @Override
    public void update(String eventType, Account account) {
        System.out.println(
                "|> [УВЕДОМЛЕНИЕ]: Событие '" + eventType +
                        "' на счете " + account.getId() +
                        ". Новый баланс: " + account.getBalance()
        );
    }
}