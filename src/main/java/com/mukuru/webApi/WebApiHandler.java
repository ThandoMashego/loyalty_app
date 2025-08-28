package com.mukuru.webApi;

import com.mukuru.model.Transaction;
import com.mukuru.model.User;
import com.mukuru.service.LoyaltyService;

import io.javalin.http.Context;


import java.util.List;
import java.util.Map;

public class WebApiHandler {
    private static final LoyaltyService loyaltyService = new LoyaltyService();

    //Register a customer or user:
    public static void registerUser(Context context){
        Map<String, String> body = context.bodyAsClass(Map.class);
        String phone = body.get("phone");
        String name = body.getOrDefault("name", "Customer");

        User user = LoyaltyService.registerUser(phone, name);
        context.json(user);
    }

    // Send money (creates a transaction and earns points)
    public static void sendMoney(Context context) {
        Long userId = Long.parseLong(context.pathParam("userId"));
        Map<String, Object> body = context.bodyAsClass(Map.class);
        double amount = Double.parseDouble(body.get("amount").toString());

        try {
            Transaction transaction = loyaltyService.sendMoney(userId, amount);
            context.json(transaction);
        } catch (IllegalArgumentException e) {
            context.status(404).result(e.getMessage());
        }
    }

    // Get user points
    public static void getPoints(Context context) {
        Long userId = Long.parseLong(context.pathParam("userId"));
        try {
            int points = LoyaltyService.getPoints(userId);
            context.json(Map.of("userId", userId, "points", points));
        } catch (IllegalArgumentException e) {
            context.status(404).result(e.getMessage());
        }
    }

    // Get transactions of a user
    public static void getTransactions(Context context) {
        Long userId = Long.parseLong(context.pathParam("userId"));
        List<Transaction> txns = LoyaltyService.getTransactions(userId);
        context.json(txns);
    }

    // Get all rewards
    public static void getRewards(Context context) {
        context.json(loyaltyService.getRewards());
    }





}

