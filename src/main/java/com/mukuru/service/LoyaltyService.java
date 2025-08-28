package com.mukuru.service;

import com.mukuru.model.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class LoyaltyService {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Reward> rewards = new HashMap<>();
    private final Map<Long, List<Transaction>> transactions = new HashMap<>();

    private final AtomicLong userIdSeq = new AtomicLong(1);
    private final AtomicLong txnIdSeq = new AtomicLong(1);
    private final AtomicLong rewardIdSeq = new AtomicLong(1);

    public User registerUser(String phone, String name) {
        return users.values().stream()
                .filter(u -> u.getPhone().equals(phone))
                .findFirst()
                .orElseGet(() -> {
                    User u = new User(
                            userIdSeq.getAndIncrement(),
                            phone,
                            (name == null || name.isBlank()) ? "Customer" : name
                    );
                    users.put(u.getId(), u);
                    return u;
                });
    }

    public Transaction sendMoney(Long userId, double amount) {
        User user = users.get(userId);
        if (user == null) throw new IllegalArgumentException("User not found");

        int points = (int) Math.floor(amount / 100.0); // 1 point per R100
        user.setPoints(user.getPoints() + points);
        updateTier(user);

        Transaction txn = new Transaction(txnIdSeq.getAndIncrement(), userId, amount, points);
        transactions.computeIfAbsent(userId, k -> new ArrayList<>()).add(txn);
        return txn;
    }

    public int getPoints(Long userId) {
        User user = users.get(userId);
        if (user == null) throw new IllegalArgumentException("User not found");
        return user.getPoints();
    }

    public List<Transaction> getTransactions(Long userId) {
        return transactions.getOrDefault(userId, Collections.emptyList());
    }

    public List<Reward> getRewards() {
        return new ArrayList<>(rewards.values());
    }

    public void addReward(String name, int cost, String desc, String icon, int stock) {
        Reward r = new Reward(rewardIdSeq.getAndIncrement(), name, cost, desc, icon, stock);
        rewards.put(r.getId(), r);
    }

    public String redeemReward(Long userId, Long rewardId) {
        User user = users.get(userId);
        Reward reward = rewards.get(rewardId);
        if (user == null || reward == null) throw new IllegalArgumentException("Invalid user or reward");

        if (user.getPoints() < reward.getCost()) return "Not enough points";
        if (reward.getStock() <= 0) return "Out of stock";

        user.setPoints(user.getPoints() - reward.getCost());
        reward.setStock(reward.getStock() - 1);
        updateTier(user);
        return "Redeemed: " + reward.getName();
    }

    private void updateTier(User user) {
        int pts = user.getPoints();
        if (pts >= 1000) user.setTier("Platinum");
        else if (pts >= 500) user.setTier("Gold");
        else if (pts >= 200) user.setTier("Silver");
        else user.setTier("Bronze");
    }
}
