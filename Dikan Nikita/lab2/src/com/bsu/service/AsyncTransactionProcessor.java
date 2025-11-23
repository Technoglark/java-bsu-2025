package com.bsu.service;
import com.bsu.model.Transaction;
import com.bsu.processor.ProcessTransactionCommand;
import com.bsu.service.interfaces.TransactionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AsyncTransactionProcessor {
    private static final AsyncTransactionProcessor INSTANCE = new AsyncTransactionProcessor();

    private final ExecutorService threadPool;


    private AsyncTransactionProcessor() {
        this.threadPool = Executors.newFixedThreadPool(10);
        System.out.println("AsyncTransactionProcessor инициализирован с 10 потоками.");
    }

    public static AsyncTransactionProcessor getInstance() {
        return INSTANCE;
    }

    public void submit(Transaction transaction, TransactionService service) {
        ProcessTransactionCommand command = new ProcessTransactionCommand(transaction, service);
        threadPool.submit(command);
    }

    public void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
