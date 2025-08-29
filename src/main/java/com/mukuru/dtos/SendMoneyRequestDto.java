package com.mukuru.dtos;

import java.math.BigDecimal;

public class SendMoneyRequestDto {
        public Long id;
        public BigDecimal amount;     // use BigDecimal for money
        public Integer points;        // ignored (computed server-side)
        public String date;           // yyyy-MM-dd
        public String recipient;
        public String country;
}
