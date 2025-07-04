package com.arka.supplier_service.use_case;

import com.arka.supplier_service.application.ports.output.SupplierPersistencePort;
import com.arka.supplier_service.application.usecase.SupplierServiceImpl;
import com.arka.supplier_service.domain.exception.supplier.SupplierNotFoundByNameException;
import com.arka.supplier_service.domain.exception.supplier.SupplierNotFoundException;
import com.arka.supplier_service.domain.model.Supplier;
import com.arka.supplier_service.domain.service.EmailValidationService;
import com.arka.supplier_service.domain.service.PhoneValidationService;
import com.arka.supplier_service.infraestructure.config.ValidationException;
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
import static org.mockito.Mockito.*;

class SupplierServiceImplTest {
    @Mock
    private SupplierPersistencePort supplierPersistencePort;
    @Mock
    private EmailValidationService emailValidationService;
    @Mock
    private PhoneValidationService phoneValidationService;
    @InjectMocks
    private SupplierServiceImpl supplierService;

    private Supplier validSupplier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validSupplier = new Supplier(
                null,
                "Proveedor 1",
                "123456789",
                "Contacto",
                "test@email.com",
                "+573001112233",
                true,
                null
        );
    }

    @Test
    void createSupplier_success() {
        when(emailValidationService.isValidEmail(anyString())).thenReturn(true);
        when(phoneValidationService.isValidPhone(anyString())).thenReturn(true);
        when(supplierPersistencePort.save(any(Supplier.class))).thenReturn(Mono.just(validSupplier));

        Supplier result = supplierService.create(validSupplier).block();
        assertNotNull(result);
        assertEquals("Proveedor 1", result.getName());
    }

    @Test
    void createSupplier_invalidEmail() {
        when(emailValidationService.isValidEmail(anyString())).thenReturn(false);
        when(phoneValidationService.isValidPhone(anyString())).thenReturn(true);

        assertThrows(ValidationException.class, () -> supplierService.create(validSupplier).block());
    }

    @Test
    void createSupplier_invalidPhone() {
        when(emailValidationService.isValidEmail(anyString())).thenReturn(true);
        when(phoneValidationService.isValidPhone(anyString())).thenReturn(false);

        assertThrows(ValidationException.class, () -> supplierService.create(validSupplier).block());
    }

    @Test
    void updateSupplier_success() {
        UUID id = UUID.randomUUID();
        Supplier existing = new Supplier(id, "Proveedor 1", "123", "Contacto", "test@email.com", "+573001112233", true, LocalDateTime.now());
        when(emailValidationService.isValidEmail(anyString())).thenReturn(true);
        when(phoneValidationService.isValidPhone(anyString())).thenReturn(true);
        when(supplierPersistencePort.findById(id)).thenReturn(Mono.just(existing));
        when(supplierPersistencePort.update(eq(id), any(Supplier.class))).thenReturn(Mono.just(existing));

        Supplier result = supplierService.update(id, validSupplier).block();
        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void updateSupplier_notFound() {
        UUID id = UUID.randomUUID();
        when(emailValidationService.isValidEmail(anyString())).thenReturn(true);
        when(phoneValidationService.isValidPhone(anyString())).thenReturn(true);
        when(supplierPersistencePort.findById(id)).thenReturn(Mono.empty());

        assertThrows(SupplierNotFoundException.class, () -> supplierService.update(id, validSupplier).block());
    }

    @Test
    void findById_found() {
        UUID id = UUID.randomUUID();
        Supplier supplier = new Supplier(id, "Proveedor 1", "123", "Contacto", "test@email.com", "+573001112233", true, LocalDateTime.now());
        when(supplierPersistencePort.findById(id)).thenReturn(Mono.just(supplier));
        Supplier result = supplierService.findById(id).block();
        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void findById_notFound() {
        UUID id = UUID.randomUUID();
        when(supplierPersistencePort.findById(id)).thenReturn(Mono.empty());
        assertThrows(SupplierNotFoundException.class, () -> supplierService.findById(id).block());
    }

    @Test
    void findByName_found() {
        when(supplierPersistencePort.findByName(anyString())).thenReturn(Flux.just(validSupplier));
        assertTrue(supplierService.findByName("Proveedor 1").collectList().block().size() > 0);
    }

    @Test
    void findByName_notFound() {
        when(supplierPersistencePort.findByName(anyString())).thenReturn(Flux.empty());
        assertThrows(SupplierNotFoundByNameException.class, () -> supplierService.findByName("NoExiste").collectList().block());
    }

    @Test
    void activateSupplier_success() {
        UUID id = UUID.randomUUID();
        when(supplierPersistencePort.findById(id)).thenReturn(Mono.just(validSupplier));
        when(supplierPersistencePort.activate(id)).thenReturn(Mono.empty());
        assertDoesNotThrow(() -> supplierService.activate(id).block());
    }

    @Test
    void deactivateSupplier_success() {
        UUID id = UUID.randomUUID();
        when(supplierPersistencePort.findById(id)).thenReturn(Mono.just(validSupplier));
        when(supplierPersistencePort.deactivate(id)).thenReturn(Mono.empty());
        assertDoesNotThrow(() -> supplierService.deactivate(id).block());
    }

    @Test
    void deleteSupplier_success() {
        UUID id = UUID.randomUUID();
        when(supplierPersistencePort.findById(id)).thenReturn(Mono.just(validSupplier));
        when(supplierPersistencePort.delete(id)).thenReturn(Mono.empty());
        assertDoesNotThrow(() -> supplierService.delete(id).block());
    }
}
