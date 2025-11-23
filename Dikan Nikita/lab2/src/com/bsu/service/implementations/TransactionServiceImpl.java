package com.bsu.service.implementations;

import com.bsu.model.Account;
import com.bsu.model.Actions;
import com.bsu.model.Transaction;
import com.bsu.processor.*;
import com.bsu.repository.AccountRepository;
import com.bsu.service.interfaces.AccountService;
import com.bsu.service.interfaces.LoggingService;
import com.bsu.service.interfaces.TransactionService;
import com.bsu.service.observer.EventListener;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionServiceImpl implements TransactionService {

    private final LoggingService logger;
    private final Map<Actions, TransactionOperation> operations;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    private final Map<UUID, Lock> accountLocks = new ConcurrentHashMap<>();

    public TransactionServiceImpl(LoggingService logger, AccountService accountService, AccountRepository accountRepository) {
        this.logger = logger;
        this.accountService = accountService;
        this.accountRepository = accountRepository;

        operations = new EnumMap<>(Actions.class);
        operations.put(Actions.REFILL, new RefillOperation());
        operations.put(Actions.WITHDRAWING, new WithdrawOperation());
        operations.put(Actions.TRANSFER, new TransferOperation());
        operations.put(Actions.FREEZING, new FreezeOperation(this.accountService));
    }

    @Override
    public void processTransaction(Transaction transaction) {
        if (transaction.getType() == Actions.TRANSFER) {
            processTransfer(transaction);
        } else {
            processSingleAccountOperation(transaction);
        }
    }

    private void processSingleAccountOperation(Transaction transaction) {
        UUID accountId = transaction.getFromAccount().getId();
        Lock lock = accountLocks.computeIfAbsent(accountId, k -> new ReentrantLock());
        lock.lock();
        try {
            executeTransactionLogic(transaction);
        } finally {
            lock.unlock();
        }
    }

    private void processTransfer(Transaction transaction) {
        UUID fromId = transaction.getFromAccount().getId();
        UUID toId = transaction.getToAccount().getId();

        UUID firstLockId = (fromId.compareTo(toId) < 0) ? fromId : toId;
        UUID secondLockId = (fromId.compareTo(toId) < 0) ? toId : fromId;

        Lock firstLock = accountLocks.computeIfAbsent(firstLockId, k -> new ReentrantLock());
        Lock secondLock = accountLocks.computeIfAbsent(secondLockId, k -> new ReentrantLock());

        firstLock.lock();
        secondLock.lock();
        try {
            executeTransactionLogic(transaction);
        } finally {
            secondLock.unlock();
            firstLock.unlock();
        }
    }

    private void executeTransactionLogic(Transaction transaction) {
        logger.log("Начало обработки транзакции " + transaction.getId());

        Account fromAccountDB = accountRepository.findById(transaction.getFromAccount().getId())
                .orElseThrow(() -> new IllegalStateException("Счет-отправитель " + transaction.getFromAccount().getId() + " не найден в БД!"));

        for (EventListener listener : transaction.getFromAccount().getListeners()) {
            fromAccountDB.subscribe(listener);
        }

        Transaction dbTransaction;

        if (transaction.getType() == Actions.TRANSFER) {
            Account toAccountDB = accountRepository.findById(transaction.getToAccount().getId())
                    .orElseThrow(() -> new IllegalStateException("Счет-получатель " + transaction.getToAccount().getId() + " не найден в БД!"));

            for (EventListener listener : transaction.getToAccount().getListeners()) {
                toAccountDB.subscribe(listener);
            }

            dbTransaction = new Transaction(transaction.getAmount(), fromAccountDB, toAccountDB);
        } else {
            dbTransaction = new Transaction(transaction.getType(), transaction.getAmount(), fromAccountDB);
        }

        TransactionOperation operation = operations.get(dbTransaction.getType());
        if (operation != null) {
            operation.execute(dbTransaction);
            accountRepository.save(dbTransaction.getFromAccount());
            if (dbTransaction.getType() == Actions.TRANSFER) {
                accountRepository.save(dbTransaction.getToAccount());
            }
            logger.log("Транзакция " + transaction.getId() + " успешно обработана и сохранена в БД.");
        } else {
            logger.log("Ошибка: Не найден обработчик для типа транзакции " + transaction.getType());
        }
    }
}