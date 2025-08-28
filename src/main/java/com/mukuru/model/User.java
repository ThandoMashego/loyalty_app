package com.mukuru.model;

import java.time.LocalDateTime;

public class User {
    private long id;
    private String phone;
    private String name;
    private int points;
    private String tier;
    private LocalDateTime createdAt;

    public User(long id, String phone, String name, int points, String tier, LocalDateTime createdAt) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.points = points;
        this.tier = tier;
        this.createdAt = createdAt;
    }


    public User(long id, String phone, String name) {
        this(id, phone, name, 0, "Bronze", LocalDateTime.now());
    }

    public long getId() { return id; }
    public String getPhone() { return phone; }
    public String getName() { return name; }
    public int getPoints() { return points; }
    public String getTier() { return tier; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setPoints(int points) { this.points = points; }
    public void setTier(String tier) { this.tier = tier; }
}
