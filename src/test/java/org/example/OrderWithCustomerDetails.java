package org.example;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderWithCustomerDetails(
    Long customerOrderId, String fullName, LocalDateTime orderDate, BigDecimal totalAmount) {}
