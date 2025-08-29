package com.mukuru.webApi;

import com.mukuru.dtos.SendMoneyRequestDto;
import com.mukuru.dtos.SendMoneyResponseDto;
import com.mukuru.model.Transaction;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class WebApiHandler {
    public static void registerCustomer(@NotNull Context context) {
        String id = context.pathParam("id");
        context.json("Registering customer with ID: " + id);

        context.status(201);
    }

    public static void sendMoney(@NotNull Context context) {
        final String pathId = context.pathParam("id");

        // Parse & validate body
        SendMoneyRequestDto req = context.bodyValidator(SendMoneyRequestDto.class)
                .check(r -> r.amount != null, "amount is required")
                .check(r -> r.amount != null && r.amount.compareTo(BigDecimal.ZERO) > 0, "amount must be > 0")
                .check(r -> r.recipient != null && !r.recipient.isBlank(), "recipient is required")
                .get();

        // Compute points server-side to avoid client tampering
        int earnedPoints = req.amount
                .divide(new BigDecimal("100"), 0, RoundingMode.DOWN)
                .intValue();

        // Pick date: use supplied or default to today
        String isoDate = (req.date != null && !req.date.isBlank())
                ? req.date
                : LocalDate.now().toString();

        // TODO: Business rules (examples)
        // - Check sender balance / daily limit / KYC status
        // - Validate recipient existence, same/different customer, etc.
        // - Perform ledger entries (debit/credit) if you keep balances

        // Create transaction
        Transaction tx = new Transaction();
        tx.setId(ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE));
        tx.setAmount(req.amount.doubleValue());
        tx.setPoints(earnedPoints);
        // tx.date = isoDate;
        // tx.recipient = req.recipient;
        // tx.country = (req.country == null || req.country.isBlank()) ? "South Africa" : req.country;

        // Persist transaction to database

        // Build response
        SendMoneyResponseDto resp = new SendMoneyResponseDto();
        resp.success = true;
        resp.message = "Money sent successfully";
        resp.transaction = tx;
        resp.pointsEarned = earnedPoints;
        resp.pointsBalance = 0;

        context.status(HttpStatus.CREATED).json(resp);
    }
}

