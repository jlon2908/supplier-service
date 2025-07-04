package com.arka.supplier_service.use_case;

import com.arka.supplier_service.application.ports.output.StockEventPublisher;
import com.arka.supplier_service.application.ports.output.SupplierReceiptPersistencePort;
import com.arka.supplier_service.application.usecase.SupplierReceiptServiceImpl;
import com.arka.supplier_service.domain.exception.SupplierReceiptFilterNoMatchException;
import com.arka.supplier_service.domain.exception.SupplierReceiptNotFoundBySupplierNameException;
import com.arka.supplier_service.domain.exception.supplierOrderRecipt.DuplicateSupplierReceiptForOrderException;
import com.arka.supplier_service.domain.exception.supplierOrderRecipt.SupplierReceiptNotFoundByOrderCodeException;
import com.arka.supplier_service.domain.exception.supplierOrderRecipt.SupplierReceiptNotFoundByReceivedByException;
import com.arka.supplier_service.domain.model.SupplierReceipt;
import com.arka.supplier_service.domain.model.SupplierReceiptDetail;
import com.arka.supplier_service.domain.service.ReceivedByValidationService;
import com.arka.supplier_service.infraestructure.config.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplierReceiptServiceImplTest {
    @Mock
    private SupplierReceiptPersistencePort persistencePort;
    @Mock
    private ReceivedByValidationService receivedByValidationService;
    @Mock
    private StockEventPublisher stockEventPublisher;
    @InjectMocks
    private SupplierReceiptServiceImpl service;

    private SupplierReceipt validReceipt;
    private SupplierReceiptDetail detail;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        detail = SupplierReceiptDetail.builder()
                .id(UUID.randomUUID())
                .sku("SKU123")
                .quantityReceived(5)
                .build();
        validReceipt = SupplierReceipt.builder()
                .id(UUID.randomUUID())
                .supplierOrderId(UUID.randomUUID())
                .receivedBy("Juan Perez")
                .receivedDate(LocalDateTime.now())
                .details(List.of(detail))
                .build();
    }

    @Test
    void createReceipt_success() {
        when(receivedByValidationService.isValid(anyString())).thenReturn(true);
        when(persistencePort.save(any(SupplierReceipt.class))).thenReturn(Mono.just(validReceipt));
        doNothing().when(stockEventPublisher).publishStockReceivedEvent(any(SupplierReceipt.class));

        SupplierReceipt result = service.create(validReceipt).block();
        assertNotNull(result);
        assertEquals("Juan Perez", result.getReceivedBy());
    }

    @Test
    void createReceipt_invalidReceivedBy() {
        when(receivedByValidationService.isValid(anyString())).thenReturn(false);
        assertThrows(ValidationException.class, () -> service.create(validReceipt).block());
    }

    @Test
    void createReceipt_duplicate() {
        when(receivedByValidationService.isValid(anyString())).thenReturn(true);
        when(persistencePort.save(any(SupplierReceipt.class))).thenReturn(Mono.error(new RuntimeException("duplicate key value violates unique constraint \"supplierreceipt_supplierorderid_key\"")));
        assertThrows(DuplicateSupplierReceiptForOrderException.class, () -> service.create(validReceipt).block());
    }

    @Test
    void findBySupplierName_found() {
        when(persistencePort.findBySupplierName(anyString())).thenReturn(Flux.just(validReceipt));
        List<SupplierReceipt> result = service.findBySupplierName("Proveedor").collectList().block();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findBySupplierName_notFound() {
        when(persistencePort.findBySupplierName(anyString())).thenReturn(Flux.empty());
        assertThrows(SupplierReceiptNotFoundBySupplierNameException.class, () -> service.findBySupplierName("NoExiste").collectList().block());
    }

    @Test
    void findByOrderCode_found() {
        when(persistencePort.findByOrderCode(anyString())).thenReturn(Flux.just(validReceipt));
        List<SupplierReceipt> result = service.findByOrderCode("ORD-001").collectList().block();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findByOrderCode_notFound() {
        when(persistencePort.findByOrderCode(anyString())).thenReturn(Flux.empty());
        assertThrows(SupplierReceiptNotFoundByOrderCodeException.class, () -> service.findByOrderCode("ORD-404").collectList().block());
    }

    @Test
    void findByReceivedBy_found() {
        when(persistencePort.findByReceivedBy(anyString())).thenReturn(Flux.just(validReceipt));
        List<SupplierReceipt> result = service.findByReceivedBy("Juan Perez").collectList().block();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findByReceivedBy_notFound() {
        when(persistencePort.findByReceivedBy(anyString())).thenReturn(Flux.empty());
        assertThrows(SupplierReceiptNotFoundByReceivedByException.class, () -> service.findByReceivedBy("NoExiste").collectList().block());
    }

    @Test
    void filterReceipts_found() {
        when(persistencePort.filterReceipts(anyString(), anyString(), anyString(), any(), any())).thenReturn(Flux.just(validReceipt));
        List<SupplierReceipt> result = service.filterReceipts("Proveedor", "ORD-001", "Juan Perez", LocalDateTime.now().minusDays(1), LocalDateTime.now()).collectList().block();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void filterReceipts_notFound() {
        when(persistencePort.filterReceipts(anyString(), anyString(), anyString(), any(), any())).thenReturn(Flux.empty());
        assertThrows(SupplierReceiptFilterNoMatchException.class, () -> service.filterReceipts("Proveedor", "ORD-001", "Juan Perez", LocalDateTime.now().minusDays(1), LocalDateTime.now()).collectList().block());
    }
}

