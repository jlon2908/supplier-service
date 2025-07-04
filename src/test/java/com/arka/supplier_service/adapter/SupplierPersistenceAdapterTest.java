package com.arka.supplier_service.adapter;

import com.arka.supplier_service.domain.model.Supplier;
import com.arka.supplier_service.infraestructure.adapters.driven.db.adapter.SupplierPersistenceAdapter;
import com.arka.supplier_service.infraestructure.adapters.driven.db.entity.SupplierEntity;
import com.arka.supplier_service.infraestructure.adapters.driven.db.mapper.SupplierPersistenceMapper;
import com.arka.supplier_service.infraestructure.adapters.driven.db.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SupplierPersistenceAdapterTest {
    @Mock
    private SupplierRepository supplierRepository;
    @InjectMocks
    private SupplierPersistenceAdapter adapter;

    private Supplier supplier;
    private SupplierEntity entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
        entity = SupplierPersistenceMapper.toEntity(supplier);
    }

    @Test
    void saveSupplier_success() {
        when(supplierRepository.insertSupplier(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(Mono.empty());
        Supplier result = adapter.save(supplier).block();
        assertNotNull(result);
        assertEquals(supplier.getName(), result.getName());
    }

    @Test
    void findById_found() {
        when(supplierRepository.findById(any(UUID.class))).thenReturn(Mono.just(entity));
        Supplier result = adapter.findById(supplier.getId()).block();
        assertNotNull(result);
        assertEquals(supplier.getId(), result.getId());
    }

    @Test
    void findAll_suppliers() {
        when(supplierRepository.findAll()).thenReturn(Flux.just(entity));
        assertFalse(adapter.findAll().collectList().block().isEmpty());
    }

    @Test
    void findByName_found() {
        when(supplierRepository.findByNameContainingIgnoreCase(any())).thenReturn(Flux.just(entity));
        assertFalse(adapter.findByName("Proveedor").collectList().block().isEmpty());
    }

    @Test
    void activateSupplier_success() {
        when(supplierRepository.findById(any(UUID.class))).thenReturn(Mono.just(entity));
        when(supplierRepository.save(any(SupplierEntity.class))).thenReturn(Mono.just(entity));
        assertDoesNotThrow(() -> adapter.activate(supplier.getId()).block());
    }

    @Test
    void deactivateSupplier_success() {
        when(supplierRepository.findById(any(UUID.class))).thenReturn(Mono.just(entity));
        when(supplierRepository.save(any(SupplierEntity.class))).thenReturn(Mono.just(entity));
        assertDoesNotThrow(() -> adapter.deactivate(supplier.getId()).block());
    }

    @Test
    void deleteSupplier_success() {
        when(supplierRepository.deleteById(any(UUID.class))).thenReturn(Mono.empty());
        assertDoesNotThrow(() -> adapter.delete(supplier.getId()).block());
    }
}

