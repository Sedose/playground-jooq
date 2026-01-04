package org.example;

import java.math.BigDecimal;
import java.time.LocalDateTime;

record CustomerOrderSummary(
    String fullName, Long customerOrderId, LocalDateTime orderDate, BigDecimal totalAmount) {}
