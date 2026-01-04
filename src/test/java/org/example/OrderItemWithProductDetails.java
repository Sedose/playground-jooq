package org.example;

import java.math.BigDecimal;

record OrderItemWithProductDetails(
    Long customerOrderId, String productName, Integer quantity, BigDecimal unitPrice) {}
