package com.arka.supplier_service.infraestructure.adapters.driven.amqp.adapter;

import com.arka.supplier_service.application.ports.output.StockEventPublisher;
import com.arka.supplier_service.domain.model.SupplierReceipt;
import com.arka.supplier_service.infraestructure.adapters.driven.amqp.dto.StockItemDto;
import com.arka.supplier_service.infraestructure.adapters.driven.amqp.dto.StockReceivedEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockEventPublisherRabbitAdapter implements StockEventPublisher {

    private final RabbitTemplate rabbitTemplate;


    @Override
    public void publishStockReceivedEvent(SupplierReceipt receipt) {
        List<StockItemDto> items = receipt.getDetails().stream()
                .map(detail -> new StockItemDto(detail.getSku(), detail.getQuantityReceived(), receipt.getWarehouseCode()))
                .collect(Collectors.toList());

        StockReceivedEventDto event = new StockReceivedEventDto(items);

        // Log para depuración
        System.out.println("[DEBUG] Enviando evento StockReceivedEventDto: " + event);
        System.out.println("[DEBUG] warehouseCode (por item): " + items.stream().map(StockItemDto::getWarehouseCode).toList());

        rabbitTemplate.convertAndSend("stock", "stock.received", event);
    }
}