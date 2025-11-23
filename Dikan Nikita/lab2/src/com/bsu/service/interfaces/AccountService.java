package com.bsu.service.interfaces;
import com.bsu.model.Account;

public interface AccountService {
    void freezeAccount(Account account);
    void unfreezeAccount(Account account);
}
