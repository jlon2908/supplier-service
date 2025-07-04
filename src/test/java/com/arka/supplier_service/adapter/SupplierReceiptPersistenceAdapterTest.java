package com.arka.supplier_service.adapter;

import com.arka.supplier_service.domain.model.SupplierReceipt;
import com.arka.supplier_service.domain.model.SupplierReceiptDetail;
import com.arka.supplier_service.infraestructure.adapters.driven.db.adapter.SupplierReceiptPersistenceAdapter;
import com.arka.supplier_service.infraestructure.adapters.driven.db.entity.SupplierReceiptDetailEntity;
import com.arka.supplier_service.infraestructure.adapters.driven.db.entity.SupplierReceiptEntity;
import com.arka.supplier_service.infraestructure.adapters.driven.db.mapper.SupplierReceiptPersistenceMapper;
import com.arka.supplier_service.infraestructure.adapters.driven.db.repository.SupplierReceiptDetailRepository;
import com.arka.supplier_service.infraestructure.adapters.driven.db.repository.SupplierReceiptRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class SupplierReceiptPersistenceAdapterTest {
    @Mock
    private SupplierReceiptRepository receiptRepository;
    @Mock
    private SupplierReceiptDetailRepository detailRepository;
    @InjectMocks
    private SupplierReceiptPersistenceAdapter adapter;

    private SupplierReceipt receipt;
    private SupplierReceiptEntity receiptEntity;
    private SupplierReceiptDetail detail;
    private SupplierReceiptDetailEntity detailEntity;
    private UUID receiptId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        receiptId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        detail = SupplierReceiptDetail.builder()
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
        receiptEntity = SupplierReceiptPersistenceMapper.toEntity(receipt);
        detailEntity = SupplierReceiptPersistenceMapper.toDetailEntity(detail);
    }

    @Test
    void saveReceipt_success() {
        when(receiptRepository.insertSupplierReceipt(any(), any(), any(), any(), any())).thenReturn(Mono.empty());
        when(detailRepository.insertSupplierReceiptDetail(any(), any(), any(), any())).thenReturn(Mono.empty());
        SupplierReceipt result = adapter.save(receipt).block();
        assertNotNull(result);
        assertEquals("Juan Perez", result.getReceivedBy());
    }

    @Test
    void findBySupplierName_found() {
        when(receiptRepository.findBySupplierName(anyString())).thenReturn(Flux.just(receiptEntity));
        when(detailRepository.findBySupplierReceiptId(receiptId)).thenReturn(Flux.just(detailEntity));
        List<SupplierReceipt> result = adapter.findBySupplierName("Proveedor").collectList().block();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findByOrderCode_found() {
        when(receiptRepository.findByOrderCode(anyString())).thenReturn(Flux.just(receiptEntity));
        when(detailRepository.findBySupplierReceiptId(receiptId)).thenReturn(Flux.just(detailEntity));
        List<SupplierReceipt> result = adapter.findByOrderCode("ORD-001").collectList().block();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findByReceivedBy_found() {
        when(receiptRepository.findByReceivedBy(anyString())).thenReturn(Flux.just(receiptEntity));
        when(detailRepository.findBySupplierReceiptId(receiptId)).thenReturn(Flux.just(detailEntity));
        List<SupplierReceipt> result = adapter.findByReceivedBy("Juan Perez").collectList().block();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void filterReceipts_found() {
        when(receiptRepository.filterReceipts(anyString(), anyString(), anyString(), any(), any())).thenReturn(Flux.just(receiptEntity));
        when(detailRepository.findBySupplierReceiptId(receiptId)).thenReturn(Flux.just(detailEntity));
        List<SupplierReceipt> result = adapter.filterReceipts("Proveedor", "ORD-001", "Juan Perez", LocalDateTime.now().minusDays(1), LocalDateTime.now()).collectList().block();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}

