package com.bsu.ui;

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
import com.bsu.service.observer.EventListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.UUID;

public class GuiMain {
    private static final UUID ACCOUNT_A_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ACCOUNT_B_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");


    private AccountRepository accountRepository;
    private TransactionService transactionService;
    private AsyncTransactionProcessor processor;
    private Account accountA;
    private Account accountB;


    private JLabel balanceALabel;
    private JLabel statusALabel;
    private JLabel balanceBLabel;
    private JLabel statusBLabel;
    private JFrame frame;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(GuiMain::new);
    }

    public GuiMain() {
        initBackend();
        createAndShowGui();
    }


    private void initBackend() {
        DatabaseManager.getInstance();
        LoggingService logger = ConsoleLogger.getInstance();

        accountRepository = new JdbcAccountRepository();
        AccountService accountService = new AccountServiceImpl(logger);
        transactionService = new TransactionServiceImpl(logger, accountService, accountRepository);
        processor = AsyncTransactionProcessor.getInstance();

        accountA = accountRepository.findById(ACCOUNT_A_ID).orElseGet(() -> {
            Account newAcc = new Account(); newAcc.setId(ACCOUNT_A_ID);
            accountRepository.save(newAcc); return newAcc;
        });
        accountB = accountRepository.findById(ACCOUNT_B_ID).orElseGet(() -> {
            Account newAcc = new Account(); newAcc.setId(ACCOUNT_B_ID);
            accountRepository.save(newAcc); return newAcc;
        });

        GuiUpdater updater = new GuiUpdater();
        accountA.subscribe(updater);
        accountB.subscribe(updater);
    }

    private void createAndShowGui() {
        frame = new JFrame("Банковская cистема");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 600);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Приложение закрывается. Завершение работы фоновых потоков...");
                if (processor != null) {
                    processor.shutdown();
                }
            }
        });

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        balanceALabel = new JLabel();
        statusALabel = new JLabel();
        balanceBLabel = new JLabel();
        statusBLabel = new JLabel();

        JPanel accountAPanel = createAccountPanel("Счет A", accountA, accountB, balanceALabel, statusALabel);
        JPanel accountBPanel = createAccountPanel("Счет B", accountB, accountA, balanceBLabel, statusBLabel);

        topPanel.add(accountAPanel);
        topPanel.add(accountBPanel);

        JButton stressTestButton = new JButton("Запустить стресс-тест на Счете A (1000 пополнений/снятий)");
        stressTestButton.setFont(new Font("Arial", Font.BOLD, 14));
        stressTestButton.addActionListener(e -> runStressTest());
        JPanel stressPanel = new JPanel(new BorderLayout());
        stressPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        stressPanel.add(stressTestButton, BorderLayout.CENTER);

        JPanel controlsContainer = new JPanel(new BorderLayout());
        controlsContainer.add(topPanel, BorderLayout.CENTER);
        controlsContainer.add(stressPanel, BorderLayout.SOUTH);

        JTextArea consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(consoleArea);

        ConsoleLogger.getInstance().setOutputArea(consoleArea);
        frame.getContentPane().add(controlsContainer, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        updateUi();

        System.out.println("GUI инициализирован. Система готова к работе.");

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createAccountPanel(String title, Account primary, Account secondary, JLabel balanceLabel, JLabel statusLabel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), title, TitledBorder.LEFT, TitledBorder.TOP));

        JTextField amountField = new JTextField(10);
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, amountField.getPreferredSize().height));

        JButton depositBtn = new JButton("Пополнить");
        JButton withdrawBtn = new JButton("Снять");
        JButton transferBtn = new JButton("Перевести на другой счет");
        JButton freezeBtn = new JButton("Заморозить/Разморозить");

        depositBtn.addActionListener(e -> {
            try {
                processor.submit(TransactionFactory.createRefillTransaction(primary, new BigDecimal(amountField.getText())), transactionService);
            } catch (Exception ex) { System.err.println("Ошибка ввода: " + ex.getMessage()); }
        });

        withdrawBtn.addActionListener(e -> {
            try {
                processor.submit(TransactionFactory.createWithdrawTransaction(primary, new BigDecimal(amountField.getText())), transactionService);
            } catch (Exception ex) { System.err.println("Ошибка ввода: " + ex.getMessage()); }
        });

        transferBtn.addActionListener(e -> {
            try {
                processor.submit(TransactionFactory.createTransferTransaction(primary, secondary, new BigDecimal(amountField.getText())), transactionService);
            } catch (Exception ex) { System.err.println("Ошибка ввода: " + ex.getMessage()); }
        });

        freezeBtn.addActionListener(e -> {
            Transaction toggleFreezeTx = TransactionFactory.createFreezeTransaction(primary);
            processor.submit(toggleFreezeTx, transactionService);
        });
        panel.add(new JLabel("ID: " + primary.getId().toString().substring(0, 13) + "..."));
        panel.add(balanceLabel);
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Сумма:"));
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(5));
        panel.add(depositBtn);
        panel.add(withdrawBtn);
        panel.add(transferBtn);
        panel.add(freezeBtn);

        return panel;
    }

    private void runStressTest() {
        System.out.println("\nЗАПУСК СТРЕСС-ТЕСТА НА СЧЕТЕ A");
        System.out.println("Отправка 1000 транзакций...");
        for (int i = 0; i < 500; i++) {
            processor.submit(TransactionFactory.createRefillTransaction(accountA, BigDecimal.TEN), transactionService);
            processor.submit(TransactionFactory.createWithdrawTransaction(accountA, new BigDecimal("5")), transactionService);
        }
    }

    private void updateUi() {
        accountRepository.findById(ACCOUNT_A_ID).ifPresent(acc -> {
            balanceALabel.setText("Баланс: " + acc.getBalance());
            statusALabel.setText("Статус: " + (acc.isFrozen() ? "Заморожен" : "Активен"));
        });
        accountRepository.findById(ACCOUNT_B_ID).ifPresent(acc -> {
            balanceBLabel.setText("Баланс: " + acc.getBalance());
            statusBLabel.setText("Статус: " + (acc.isFrozen() ? "Заморожен" : "Активен"));
        });
    }

    private class GuiUpdater implements EventListener {
        @Override
        public void update(String eventType, Account account) {
            SwingUtilities.invokeLater(() -> {
                if (account.getId().equals(ACCOUNT_A_ID)) {
                    balanceALabel.setText("Баланс: " + account.getBalance());
                    statusALabel.setText("Статус: " + (account.isFrozen() ? "Заморожен" : "Активен"));
                } else if (account.getId().equals(ACCOUNT_B_ID)) {
                    balanceBLabel.setText("Баланс: " + account.getBalance());
                    statusBLabel.setText("Статус: " + (account.isFrozen() ? "Заморожен" : "Активен"));
                }
            });
        }
    }
}
