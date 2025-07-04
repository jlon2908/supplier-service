package com.arka.supplier_service.infraestructure.adapters.driven.amqp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockItemDto {
    private String sku;
    private Integer quantity;
}