package com.bsu.service.observer;

import com.bsu.model.Account;

public interface EventListener {
    void update(String eventType, Account account);
}
