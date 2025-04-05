package org.example.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDto(Long id, LocalDateTime orderDate, BigDecimal totalAmount) {}
