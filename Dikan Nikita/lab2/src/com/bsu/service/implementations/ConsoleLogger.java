package com.bsu.service.implementations;

import com.bsu.service.interfaces.LoggingService;
import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ConsoleLogger implements LoggingService {

    private static final ConsoleLogger INSTANCE = new ConsoleLogger();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private JTextArea outputArea = null;

    private ConsoleLogger() {}

    public static ConsoleLogger getInstance() {
        return INSTANCE;
    }

    public void setOutputArea(JTextArea textArea) {
        this.outputArea = textArea;
    }

    @Override
    public void log(String message) {
        String formattedMessage = String.format("[LOG %s]: %s\n", LocalDateTime.now().format(FORMATTER), message);
        System.out.print(formattedMessage);

        if (outputArea != null) {
            SwingUtilities.invokeLater(() -> {
                outputArea.append(formattedMessage);
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            });
        }
    }
}