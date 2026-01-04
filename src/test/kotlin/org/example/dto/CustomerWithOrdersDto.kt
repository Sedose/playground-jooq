package org.example.dto

data class CustomerWithOrdersDto(val id: Long, val fullName: String, val orders: List<OrderDto>)
