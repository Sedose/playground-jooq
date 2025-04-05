package org.example.dto;

import java.util.List;

public record CustomerWithOrdersDto(Long id, String fullName, List<OrderDto> orders) {}
