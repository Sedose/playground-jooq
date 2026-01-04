package org.example.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderWithCustomerDetails(
  val customerOrderId: Long,
  val fullName: String,
  val orderDate: LocalDateTime,
  val totalAmount: BigDecimal,
)
