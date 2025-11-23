package com.bsu.repository;

import com.bsu.database.DatabaseManager;
import com.bsu.model.Account;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class JdbcAccountRepository implements AccountRepository {

    private final Connection connection;

    public JdbcAccountRepository() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    @Override
    public Optional<Account> findById(UUID id) {
        String sql = "SELECT * FROM accounts WHERE id = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account();
                    account.setId(rs.getObject("id", UUID.class));
                    account.setBalance(rs.getBigDecimal("balance"));
                    account.setFrozen(rs.getBoolean("is_frozen"));
                    return Optional.of(account);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка поиска счета по ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void save(Account account) {
        String sql = "MERGE INTO accounts (id, balance, is_frozen) KEY(id) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, account.getId());
            statement.setBigDecimal(2, account.getBalance());
            statement.setBoolean(3, account.isFrozen());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка сохранения счета: " + e.getMessage());
        }
    }
}