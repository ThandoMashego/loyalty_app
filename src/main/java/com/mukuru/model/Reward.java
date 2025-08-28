package com.mukuru.model;

public class Reward {
    private long id;
    private String name;
    private int cost;
    private String description;
    private String icon;
    private int stock;

    public Reward(long id, String name, int cost, String description, String icon, int stock) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.description = description;
        this.icon = icon;
        this.stock = stock;
    }

    public boolean isAvailable() {
        return stock > 0;
    }

    public void redeem() {
        if (!isAvailable()) {
            throw new IllegalStateException("Reward is out of stock");
        }
        stock--;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public int getCost() { return cost; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public int getStock() { return stock; }

    public void setStock(int stock) { this.stock = stock; }
}
