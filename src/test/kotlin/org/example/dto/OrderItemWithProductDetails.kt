package org.example.dto

import java.math.BigDecimal

data class OrderItemWithProductDetails(
  val customerOrderId: Long,
  val productName: String,
  val quantity: Int,
  val unitPrice: BigDecimal,
)
