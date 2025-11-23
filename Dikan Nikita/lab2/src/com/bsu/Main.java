package com.bsu;

import com.bsu.database.DatabaseManager;
import com.bsu.model.Account;
import com.bsu.model.Transaction;
import com.bsu.repository.AccountRepository;
import com.bsu.repository.JdbcAccountRepository;
import com.bsu.service.AsyncTransactionProcessor;
import com.bsu.service.TransactionFactory;
import com.bsu.service.implementations.AccountServiceImpl;
import com.bsu.service.implementations.ConsoleLogger;
import com.bsu.service.implementations.TransactionServiceImpl;
import com.bsu.service.interfaces.AccountService;
import com.bsu.service.interfaces.LoggingService;
import com.bsu.service.interfaces.TransactionService;
import com.bsu.service.observer.NotificationService;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final UUID ACCOUNT_A_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ACCOUNT_B_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    public static void main(String[] args) throws InterruptedException {

        System.out.println("ИНИЦИАЛИЗАЦИЯ АБСТРАКЦИЙ");
        DatabaseManager.getInstance();
        LoggingService logger = ConsoleLogger.getInstance();
        AccountRepository accountRepository = new JdbcAccountRepository();
        AccountService accountService = new AccountServiceImpl(logger);
        TransactionService transactionService = new TransactionServiceImpl(logger, accountService, accountRepository);
        AsyncTransactionProcessor processor = AsyncTransactionProcessor.getInstance();
        System.out.println("Все абстракции инициализированы.\n");

        System.out.println("СОЗДАНИЕ АККАУНТОВ");
        Account accountA = accountRepository.findById(ACCOUNT_A_ID).orElseGet(() -> {
            Account newAcc = new Account();
            newAcc.setId(ACCOUNT_A_ID);
            accountRepository.save(newAcc);
            System.out.println("Новый счет A создан и сохранен в БД.");
            return newAcc;
        });

        Account accountB = accountRepository.findById(ACCOUNT_B_ID).orElseGet(() -> {
            Account newAcc = new Account();
            newAcc.setId(ACCOUNT_B_ID);
            accountRepository.save(newAcc);
            System.out.println("Новый счет B создан и сохранен в БД.");
            return newAcc;
        });

        System.out.println("Состояние аккаунтов при запуске:");
        System.out.println("  Аккаунт A: " + accountA);
        System.out.println("  Аккаунт B: " + accountB);

        NotificationService notifier = new NotificationService();
        accountA.subscribe(notifier);
        accountB.subscribe(notifier);
        System.out.println("NotificationService подписан на события счетов A и B.\n");


        System.out.println("ПРОВЕДЕНИЕ БАЗОВЫХ ОПЕРАЦИЙ");

        System.out.println("\n->Пополнение аккаунта A на 5000...");
        Transaction refillTx = TransactionFactory.createRefillTransaction(accountA, new BigDecimal("5000"));
        processor.submit(refillTx, transactionService);
        TimeUnit.MILLISECONDS.sleep(200);

        System.out.println("\n->Перевод 1500 с аккаунта A на аккаунт B...");
        Transaction transferTx = TransactionFactory.createTransferTransaction(accountA, accountB, new BigDecimal("1500"));
        processor.submit(transferTx, transactionService);
        TimeUnit.MILLISECONDS.sleep(200);

        System.out.println("\nПромежуточное состояние аккаунтов :");
        System.out.println("  Аккаунт A: " + accountRepository.findById(ACCOUNT_A_ID).get());
        System.out.println("  Аккаунт B: " + accountRepository.findById(ACCOUNT_B_ID).get());


        System.out.println("\nСТРЕСС-ТЕСТ ПОТОКОВ");
        System.out.println("Запускаем 1000 параллельных операций на аккаунте A: 500 пополнений по 10 и 500 снятий по 5...");

        int operationsCount = 500;
        for (int i = 0; i < operationsCount; i++) {
            Transaction refill = TransactionFactory.createRefillTransaction(accountA, BigDecimal.TEN);
            Transaction withdraw = TransactionFactory.createWithdrawTransaction(accountA, new BigDecimal("5"));
            processor.submit(refill, transactionService);
            processor.submit(withdraw, transactionService);
        }

        TimeUnit.SECONDS.sleep(5);

        Account finalAccountAState = accountRepository.findById(ACCOUNT_A_ID).get();
        BigDecimal expectedBalance = new BigDecimal("6000.00");

        System.out.println("\nРЕЗУЛЬТАТЫ СТРЕСС-ТЕСТА");
        System.out.println("Ожидаемый баланс аккаунта: " + expectedBalance);
        System.out.println("Фактический баланс аккаунта: " + finalAccountAState.getBalance());
        if (finalAccountAState.getBalance().compareTo(expectedBalance) == 0) {
            System.out.println("✅ Корректно!");
        } else {
            System.out.println("❌ Ошибка!");
        }

        processor.shutdown();
    }
}