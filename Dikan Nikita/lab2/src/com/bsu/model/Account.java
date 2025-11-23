package com.bsu.model;

import com.bsu.service.observer.EventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private UUID id;
    private BigDecimal balance;
    private boolean isFrozen;

    private final Lock lock = new ReentrantLock();

    private final List<EventListener> listeners = new ArrayList<>();

    public Account() {
        this.id = UUID.randomUUID();
        this.balance = BigDecimal.ZERO;
        this.isFrozen = false;
    }

    public void subscribe(EventListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(EventListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(String eventType) {
        for (EventListener listener : listeners) {
            listener.update(eventType, this);
        }
    }

    public void deposit(BigDecimal amount) {
        lock.lock();
        try {
            if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
                this.balance = this.balance.add(amount);
                notifyListeners("DEPOSIT");
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean withdraw(BigDecimal amount) {
        lock.lock();
        try {
            if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0 && this.balance.compareTo(amount) >= 0) {
                this.balance = this.balance.subtract(amount);
                notifyListeners("WITHDRAW");
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public BigDecimal getBalance() {
        lock.lock();
        try {
            return this.balance;
        } finally {
            lock.unlock();
        }
    }

    public boolean isFrozen() {
        lock.lock();
        try {
            return isFrozen;
        } finally {
            lock.unlock();
        }
    }

    public void setFrozen(boolean frozen) {
        lock.lock();
        try {
            isFrozen = frozen;
        } finally {
            lock.unlock();
        }
    }

    public void setBalance(BigDecimal balance) {
        lock.lock();
        try {
            this.balance = balance;
        } finally {
            lock.unlock();
        }
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id){
        this.id = id;
    }
    public List<EventListener> getListeners(){
        return listeners;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", balance=" + getBalance() +
                ", isFrozen=" + isFrozen() +
                '}';
    }

}
