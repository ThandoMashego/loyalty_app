package com.mukuru.database;

import com.mukuru.model.Reward;
import com.mukuru.model.Transaction;
import com.mukuru.model.User;

import java.sql.SQLException;
import java.util.List;


public interface LoyaltyDAO {
    User getOrCreateUser(String phone, String name) throws SQLException;


    int addTransaction(long userId, double amount) throws SQLException; // returns points earned
    int getBalance(long userId) throws SQLException;
    String getTier(long userId) throws SQLException;
    List<Transaction> getTransactions(long userId) throws SQLException;

    List<Reward> listRewards() throws SQLException;
    String redeem(long userId, long rewardId) throws SQLException;

    static int calculatePoints(double amount){
        return (int)Math.floor(amount / 100.0);
}}
