package org.example.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderDto(
  val id: Long,
  val orderDate: LocalDateTime,
  val totalAmount: BigDecimal,
)
