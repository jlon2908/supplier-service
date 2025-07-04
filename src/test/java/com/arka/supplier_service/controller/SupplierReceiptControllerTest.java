package com.arka.supplier_service.controller;

import com.arka.supplier_service.application.ports.input.SupplierReceiptServicePort;
import com.arka.supplier_service.domain.model.SupplierReceipt;
import com.arka.supplier_service.domain.model.SupplierReceiptDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = com.arka.supplier_service.infraestructure.adapters.driver.rest.controller.SupplierReceiptController.class)
class SupplierReceiptControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SupplierReceiptServicePort supplierReceiptServicePort;

    private SupplierReceipt receipt;
    private UUID receiptId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        receiptId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        SupplierReceiptDetail detail = SupplierReceiptDetail.builder()
                .id(UUID.randomUUID())
                .supplierReceiptId(receiptId)
                .sku("SKU123")
                .quantityReceived(5)
                .build();
        receipt = SupplierReceipt.builder()
                .id(receiptId)
                .supplierOrderId(orderId)
                .receivedBy("Juan Perez")
                .receivedDate(LocalDateTime.now())
                .details(List.of(detail))
                .build();
    }

    @Test
    void createSupplierReceipt() {
        when(supplierReceiptServicePort.create(any(SupplierReceipt.class))).thenReturn(Mono.just(receipt));
        webTestClient.post().uri("/api/receipts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(receipt)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.receivedBy").isEqualTo("Juan Perez");
    }

    @Test
    void getBySupplierName() {
        when(supplierReceiptServicePort.findBySupplierName(anyString())).thenReturn(Flux.just(receipt));
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/receipts/by-supplier-name").queryParam("name", "Proveedor").build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class).hasSize(1);
    }

    @Test
    void getByOrderCode() {
        when(supplierReceiptServicePort.findByOrderCode(anyString())).thenReturn(Flux.just(receipt));
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/receipts/by-order-code").queryParam("code", "ORD-001").build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class).hasSize(1);
    }

    @Test
    void getByReceivedBy() {
        when(supplierReceiptServicePort.findByReceivedBy(anyString())).thenReturn(Flux.just(receipt));
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/receipts/by-received-by").queryParam("receivedBy", "Juan Perez").build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class).hasSize(1);
    }

    @Test
    void filterReceipts() {
        when(supplierReceiptServicePort.filterReceipts(anyString(), anyString(), anyString(), any(), any())).thenReturn(Flux.just(receipt));
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/receipts/filter")
                        .queryParam("supplierName", "Proveedor")
                        .queryParam("orderCode", "ORD-001")
                        .queryParam("receivedBy", "Juan Perez")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class).hasSize(1);
    }
}

