package org.example

import java.math.BigDecimal

data class TopCustomerBySpend(
    val customerId: Long,
    val fullName: String,
    val totalSpent: BigDecimal,
)
