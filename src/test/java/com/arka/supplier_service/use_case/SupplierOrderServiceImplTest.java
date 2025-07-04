package com.arka.supplier_service.use_case;

import com.arka.supplier_service.application.ports.output.SupplierOrderPersistencePort;
import com.arka.supplier_service.application.usecase.SupplierOrderServiceImpl;
import com.arka.supplier_service.domain.exception.supplierOrder.DuplicateSupplierOrderCodeException;
import com.arka.supplier_service.domain.exception.supplierOrder.SupplierOrderNotFoundByCodeException;
import com.arka.supplier_service.domain.exception.supplierOrder.SupplierOrderNotFoundByIdException;
import com.arka.supplier_service.domain.exception.supplierOrder.SupplierOrderNotFoundBySupplierException;
import com.arka.supplier_service.domain.model.SupplierOrder;
import com.arka.supplier_service.domain.model.SupplierOrderDetail;
import com.arka.supplier_service.domain.service.SkuExistenceValidationService;
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
import static org.mockito.Mockito.*;

class SupplierOrderServiceImplTest {
    @Mock
    private SupplierOrderPersistencePort persistencePort;
    @Mock
    private SkuExistenceValidationService skuValidationService;
    @InjectMocks
    private SupplierOrderServiceImpl service;

    private SupplierOrder validOrder;
    private SupplierOrderDetail detail;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        detail = SupplierOrderDetail.builder()
                .id(UUID.randomUUID())
                .sku("SKU123")
                .quantityOrdered(2)
                .unitPrice(new BigDecimal("10.00"))
                .build();
        validOrder = SupplierOrder.builder()
                .id(UUID.randomUUID())
                .supplierId(UUID.randomUUID())
                .orderCode("ORD-001")
                .orderDate(LocalDate.now())
                .orderDetails(List.of(detail))
                .build();
    }

    @Test
    void createSupplierOrder_success() {
        when(skuValidationService.validateSkusExist(anyList())).thenReturn(Mono.empty());
        when(persistencePort.findSupplierOrderByOrderCode(anyString())).thenReturn(Mono.empty());
        when(persistencePort.saveSupplierOrder(any(SupplierOrder.class))).thenReturn(Mono.just(validOrder));

        SupplierOrder result = service.createSupplierOrder(validOrder).block();
        assertNotNull(result);
        assertEquals("ORD-001", result.getOrderCode());
    }

    @Test
    void createSupplierOrder_duplicateCode() {
        when(skuValidationService.validateSkusExist(anyList())).thenReturn(Mono.empty());
        when(persistencePort.findSupplierOrderByOrderCode(anyString())).thenReturn(Mono.just(validOrder));

        assertThrows(DuplicateSupplierOrderCodeException.class, () -> service.createSupplierOrder(validOrder).block());
    }

    @Test
    void getSupplierOrderById_found() {
        when(persistencePort.findSupplierOrderById(any(UUID.class))).thenReturn(Mono.just(validOrder));
        SupplierOrder result = service.getSupplierOrderById(validOrder.getId()).block();
        assertNotNull(result);
        assertEquals(validOrder.getId(), result.getId());
    }

    @Test
    void getSupplierOrderById_notFound() {
        when(persistencePort.findSupplierOrderById(any(UUID.class))).thenReturn(Mono.empty());
        assertThrows(SupplierOrderNotFoundByIdException.class, () -> service.getSupplierOrderById(UUID.randomUUID()).block());
    }

    @Test
    void getSupplierOrderByOrderCode_found() {
        when(persistencePort.findSupplierOrderByOrderCode(anyString())).thenReturn(Mono.just(validOrder));
        SupplierOrder result = service.getSupplierOrderByOrderCode("ORD-001").block();
        assertNotNull(result);
        assertEquals("ORD-001", result.getOrderCode());
    }

    @Test
    void getSupplierOrderByOrderCode_notFound() {
        when(persistencePort.findSupplierOrderByOrderCode(anyString())).thenReturn(Mono.empty());
        assertThrows(SupplierOrderNotFoundByCodeException.class, () -> service.getSupplierOrderByOrderCode("ORD-404").block());
    }

    @Test
    void getSupplierOrdersBySupplierId_found() {
        when(persistencePort.findSupplierOrdersBySupplierId(any(UUID.class))).thenReturn(Flux.just(validOrder));
        List<SupplierOrder> result = service.getSupplierOrdersBySupplierId(validOrder.getSupplierId()).collectList().block();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getSupplierOrdersBySupplierId_notFound() {
        when(persistencePort.findSupplierOrdersBySupplierId(any(UUID.class))).thenReturn(Flux.empty());
        assertThrows(SupplierOrderNotFoundBySupplierException.class, () -> service.getSupplierOrdersBySupplierId(UUID.randomUUID()).collectList().block());
    }

    @Test
    void getAllSupplierOrders() {
        when(persistencePort.findAllSupplierOrders()).thenReturn(Flux.just(validOrder));
        List<SupplierOrder> result = service.getAllSupplierOrders().collectList().block();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}

