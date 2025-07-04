package com.arka.supplier_service.adapter;

import com.arka.supplier_service.domain.model.SupplierReceipt;
import com.arka.supplier_service.domain.model.SupplierReceiptDetail;
import com.arka.supplier_service.infraestructure.adapters.driven.amqp.adapter.StockEventPublisherRabbitAdapter;
import com.arka.supplier_service.infraestructure.adapters.driven.amqp.dto.StockItemDto;
import com.arka.supplier_service.infraestructure.adapters.driven.amqp.dto.StockReceivedEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StockEventPublisherRabbitAdapterTest {
    @Mock
    private RabbitTemplate rabbitTemplate;
    @InjectMocks
    private StockEventPublisherRabbitAdapter adapter;

    private SupplierReceipt receipt;
    private SupplierReceiptDetail detail;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        detail = SupplierReceiptDetail.builder()
                .id(UUID.randomUUID())
                .supplierReceiptId(UUID.randomUUID())
                .sku("SKU123")
                .quantityReceived(10)
                .build();
        receipt = SupplierReceipt.builder()
                .id(UUID.randomUUID())
                .supplierOrderId(UUID.randomUUID())
                .receivedBy("Juan Perez")
                .receivedDate(LocalDateTime.now())
                .details(List.of(detail))
                .build();
    }

    @Test
    void publishStockReceivedEvent_sendsEvent() {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(StockReceivedEventDto.class));

        adapter.publishStockReceivedEvent(receipt);

        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<StockReceivedEventDto> eventCaptor = ArgumentCaptor.forClass(StockReceivedEventDto.class);

        verify(rabbitTemplate, times(1)).convertAndSend(exchangeCaptor.capture(), routingKeyCaptor.capture(), eventCaptor.capture());

        assertEquals("stock", exchangeCaptor.getValue());
        assertEquals("stock.received", routingKeyCaptor.getValue());
        assertTrue(eventCaptor.getValue() instanceof StockReceivedEventDto);
        StockReceivedEventDto event = eventCaptor.getValue();
        assertEquals(1, event.getItems().size());
        StockItemDto item = event.getItems().get(0);
        assertEquals("SKU123", item.getSku());
        assertEquals(10, item.getQuantity());
    }
}
