package org.example.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class CustomerOrderSummary(
  val fullName: String,
  val customerOrderId: Long?,
  val orderDate: LocalDateTime?,
  val totalAmount: BigDecimal?,
)
