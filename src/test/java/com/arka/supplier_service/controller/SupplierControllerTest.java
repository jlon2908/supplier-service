package com.arka.supplier_service.controller;

import com.arka.supplier_service.domain.model.Supplier;
import com.arka.supplier_service.application.ports.input.SupplierServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = com.arka.supplier_service.infraestructure.adapters.driver.rest.controller.SupplierController.class)
class SupplierControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SupplierServicePort supplierServicePort;

    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplier = new Supplier(
                UUID.randomUUID(),
                "Proveedor Test",
                "123456789",
                "Contacto",
                "test@email.com",
                "+573001112233",
                true,
                LocalDateTime.now()
        );
    }

    @Test
    void createSupplier() {
        when(supplierServicePort.create(any(Supplier.class))).thenReturn(Mono.just(supplier));
        webTestClient.post().uri("/api/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(supplier)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Proveedor Test");
    }

    @Test
    void getSupplierById() {
        when(supplierServicePort.findById(any(UUID.class))).thenReturn(Mono.just(supplier));
        webTestClient.get().uri("/api/suppliers/" + supplier.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(supplier.getId().toString());
    }

    @Test
    void getAllSuppliers() {
        when(supplierServicePort.findAll()).thenReturn(Flux.just(supplier));
        webTestClient.get().uri("/api/suppliers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Supplier.class).hasSize(1);
    }

    @Test
    void getSuppliersByName() {
        when(supplierServicePort.findByName(anyString())).thenReturn(Flux.just(supplier));
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/suppliers/filter").queryParam("name", "Proveedor Test").build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Supplier.class).hasSize(1);
    }
}
