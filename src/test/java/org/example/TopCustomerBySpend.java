package org.example;

import java.math.BigDecimal;

record TopCustomerBySpend(Long customerId, String fullName, BigDecimal totalSpent) {}
