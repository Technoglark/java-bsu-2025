package com.bsu.model;

import java.util.List;
import java.util.UUID;


public class User {
    private final UUID id;
    private String name;
    private List<Account> accounts;

    public User(String name, List<Account> accounts){
        this.id = UUID.randomUUID();
        this.name = name;
        this.accounts = accounts;
    }

    public UUID getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setAccounts(List<Account> accounts){
        this.accounts = accounts;
    }


    @Override
    public String toString() {
        return "com.bsu.model.User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", bills=" + accounts.toString() +
                '}';
    }
}
