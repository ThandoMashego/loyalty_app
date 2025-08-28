package com.mukuru.webApi;

import com.mukuru.model.User;
import com.mukuru.service.LoyaltyService;

import javax.naming.Context;
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
}

