package com.arka.supplier_service.adapter;

import com.arka.supplier_service.domain.model.SupplierOrder;
import com.arka.supplier_service.domain.model.SupplierOrderDetail;
import com.arka.supplier_service.infraestructure.adapters.driven.db.adapter.SupplierOrderPersistenceAdapter;
import com.arka.supplier_service.infraestructure.adapters.driven.db.entity.SupplierOrderDetailEntity;
import com.arka.supplier_service.infraestructure.adapters.driven.db.entity.SupplierOrderEntity;
import com.arka.supplier_service.infraestructure.adapters.driven.db.mapper.SupplierOrderPersistenceMapper;
import com.arka.supplier_service.infraestructure.adapters.driven.db.repository.SupplierOrderDetailRepository;
import com.arka.supplier_service.infraestructure.adapters.driven.db.repository.SupplierOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class SupplierOrderPersistenceAdapterTest {
    @Mock
    private SupplierOrderRepository orderRepository;
    @Mock
    private SupplierOrderDetailRepository detailRepository;
    @InjectMocks
    private SupplierOrderPersistenceAdapter adapter;

    private SupplierOrder order;
    private SupplierOrderEntity orderEntity;
    private SupplierOrderDetail detail;
    private SupplierOrderDetailEntity detailEntity;
    private UUID orderId;
    private UUID supplierId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderId = UUID.randomUUID();
        supplierId = UUID.randomUUID();
        detail = SupplierOrderDetail.builder()
                .id(UUID.randomUUID())
                .supplierOrderId(orderId)
                .sku("SKU123")
                .quantityOrdered(2)
                .unitPrice(new BigDecimal("10.00"))
                .build();
        order = SupplierOrder.builder()
                .id(orderId)
                .supplierId(supplierId)
                .orderCode("ORD-001")
                .orderDate(LocalDate.now())
                .orderDetails(List.of(detail))
                .totalAmount(new BigDecimal("20.00"))
                .build();
        orderEntity = SupplierOrderEntity.builder()
                .id(orderId)
                .supplierId(supplierId)
                .orderCode("ORD-001")
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .build();
        detailEntity = SupplierOrderDetailEntity.builder()
                .id(detail.getId())
                .supplierOrderId(orderId)
                .sku(detail.getSku())
                .quantityOrdered(detail.getQuantityOrdered())
                .unitPrice(detail.getUnitPrice())
                .build();
    }

    @Test
    void saveSupplierOrder_success() {
        when(orderRepository.insertSupplierOrder(any(), any(), any(), any(), any(), any())).thenReturn(Mono.empty());
        when(detailRepository.insertSupplierOrderDetail(any(), any(), any(), any(), any())).thenReturn(Mono.empty());
        SupplierOrder result = adapter.saveSupplierOrder(order).block();
        assertNotNull(result);
        assertEquals("ORD-001", result.getOrderCode());
    }

    @Test
    void findSupplierOrderById_found() {
        when(orderRepository.findById(orderId)).thenReturn(Mono.just(orderEntity));
        when(detailRepository.findBySupplierOrderId(orderId)).thenReturn(Flux.just(detailEntity));
        SupplierOrder result = adapter.findSupplierOrderById(orderId).block();
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(1, result.getOrderDetails().size());
    }

    @Test
    void findSupplierOrdersBySupplierId_found() {
        when(orderRepository.findBySupplierId(supplierId)).thenReturn(Flux.just(orderEntity));
        when(detailRepository.findBySupplierOrderId(orderId)).thenReturn(Flux.just(detailEntity));
        List<SupplierOrder> result = adapter.findSupplierOrdersBySupplierId(supplierId).collectList().block();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findSupplierOrderByOrderCode_found() {
        when(orderRepository.findByOrderCode(anyString())).thenReturn(Mono.just(orderEntity));
        when(detailRepository.findBySupplierOrderId(orderId)).thenReturn(Flux.just(detailEntity));
        SupplierOrder result = adapter.findSupplierOrderByOrderCode("ORD-001").block();
        assertNotNull(result);
        assertEquals("ORD-001", result.getOrderCode());
    }

    @Test
    void findAllSupplierOrders_found() {
        when(orderRepository.findAll()).thenReturn(Flux.just(orderEntity));
        when(detailRepository.findBySupplierOrderId(orderId)).thenReturn(Flux.just(detailEntity));
        List<SupplierOrder> result = adapter.findAllSupplierOrders().collectList().block();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}

