package com.mukuru.service;

import com.mukuru.model.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class LoyaltyService {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Reward> rewards = new HashMap<>();
    private final Map<Long, List<Transaction>> transactions = new HashMap<>();

    private final AtomicLong userIdSequence = new AtomicLong(1);
    private final AtomicLong transactionIdSequence = new AtomicLong(1);
    private final AtomicLong rewardIdSequence = new AtomicLong(1);

    public User registerUser(String phone, String name) {
        for (User existingUser : users.values()) {
            if (existingUser.getPhone().equals(phone)) {
                return existingUser;
            }
        }

        String finalName = (name == null || name.isBlank()) ? "Customer" : name;

        User newUser = new User(
                userIdSequence.getAndIncrement(),
                phone,
                finalName
        );

        users.put(newUser.getId(), newUser);
        return newUser;
    }

    public Transaction sendMoney(Long userId, double amount) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        int pointsEarned = (int) (amount / 100.0);
        user.setPoints(user.getPoints() + pointsEarned);
        updateTier(user);

        Transaction transaction = new Transaction(
                transactionIdSequence.getAndIncrement(),
                userId,
                amount,
                pointsEarned
        );

        transactions.computeIfAbsent(userId, k -> new ArrayList<>()).add(transaction);
        return transaction;
    }

    public int getPoints(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user.getPoints();
    }

    public List<Transaction> getTransactions(Long userId) {
        return transactions.getOrDefault(userId, Collections.emptyList());
    }

    public List<Reward> getRewards() {
        return new ArrayList<>(rewards.values());
    }

    public void addReward(String name, int cost, String description, String icon, int stock) {
        Reward reward = new Reward(
                rewardIdSequence.getAndIncrement(),
                name,
                cost,
                description,
                icon,
                stock
        );
        rewards.put(reward.getId(), reward);
    }

    public String redeemReward(Long userId, Long rewardId) {
        User user = users.get(userId);
        Reward reward = rewards.get(rewardId);

        if (user == null || reward == null) {
            throw new IllegalArgumentException("Invalid user or reward");
        }

        if (user.getPoints() < reward.getCost()) {
            return "Not enough points";
        }
        if (reward.getStock() <= 0) {
            return "Out of stock";
        }

        user.setPoints(user.getPoints() - reward.getCost());
        reward.setStock(reward.getStock() - 1);
        updateTier(user);

        return "Redeemed: " + reward.getName();
    }

    private void updateTier(User user) {
        int points = user.getPoints();
        if (points >= 1000) {
            user.setTier("Platinum");
        } else if (points >= 500) {
            user.setTier("Gold");
        } else if (points >= 200) {
            user.setTier("Silver");
        } else {
            user.setTier("Bronze");
        }
    }
}
