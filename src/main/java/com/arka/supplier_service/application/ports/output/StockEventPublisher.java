package com.arka.supplier_service.application.ports.output;

import com.arka.supplier_service.domain.model.SupplierReceipt;

public interface StockEventPublisher {
    void publishStockReceivedEvent(SupplierReceipt receipt);
}