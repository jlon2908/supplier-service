package com.arka.supplier_service.controller;

import com.arka.supplier_service.application.ports.input.SupplierOrderServicePort;
import com.arka.supplier_service.domain.model.SupplierOrder;
import com.arka.supplier_service.domain.model.SupplierOrderDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = com.arka.supplier_service.infraestructure.adapters.driver.rest.controller.SupplierOrderController.class)
class SupplierOrderControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SupplierOrderServicePort supplierOrderServicePort;

    private SupplierOrder order;
    private UUID orderId;
    private UUID supplierId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        supplierId = UUID.randomUUID();
        SupplierOrderDetail detail = SupplierOrderDetail.builder()
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
    }

    @Test
    void createSupplierOrder() {
        when(supplierOrderServicePort.createSupplierOrder(any(SupplierOrder.class))).thenReturn(Mono.just(order));
        webTestClient.post().uri("/api/supplier-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(order)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.orderCode").isEqualTo("ORD-001");
    }

    @Test
    void getSupplierOrderById() {
        when(supplierOrderServicePort.getSupplierOrderById(any(UUID.class))).thenReturn(Mono.just(order));
        webTestClient.get().uri("/api/supplier-orders/" + orderId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.orderCode").isEqualTo("ORD-001");
    }

    @Test
    void getSupplierOrdersBySupplierId() {
        when(supplierOrderServicePort.getSupplierOrdersBySupplierId(any(UUID.class))).thenReturn(Flux.just(order));
        webTestClient.get().uri("/api/supplier-orders/supplier/" + supplierId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class).hasSize(1);
    }

    @Test
    void getSupplierOrderByOrderCode() {
        when(supplierOrderServicePort.getSupplierOrderByOrderCode(anyString())).thenReturn(Mono.just(order));
        webTestClient.get().uri("/api/supplier-orders/code/ORD-001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.orderCode").isEqualTo("ORD-001");
    }

    @Test
    void getAllSupplierOrders() {
        when(supplierOrderServicePort.getAllSupplierOrders()).thenReturn(Flux.just(order));
        webTestClient.get().uri("/api/supplier-orders")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class).hasSize(1);
    }
}

