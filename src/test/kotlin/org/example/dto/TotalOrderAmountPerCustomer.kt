package org.example.dto

import java.math.BigDecimal

data class TotalOrderAmountPerCustomer(
  val fullName: String,
  val totalSpent: BigDecimal,
)
