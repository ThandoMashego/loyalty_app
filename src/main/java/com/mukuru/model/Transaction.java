package com.mukuru.model;

public class Transaction {
    private Long id;
    private Long userId;
    private double amount;
    private int points;

    public Transaction() {}
    public Transaction(Long id, Long userId, double amount, int points) {
        this.id = id; this.userId = userId; this.amount = amount; this.points = points;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}
