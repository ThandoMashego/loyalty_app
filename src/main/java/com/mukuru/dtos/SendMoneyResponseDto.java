package com.mukuru.dtos;

import com.mukuru.model.Transaction;

public class SendMoneyResponseDto {
        public boolean success;
        public String message;
        public Transaction transaction;
        public int pointsEarned;
        public int pointsBalance;
}
