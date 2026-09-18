package com.shreespark.pos_api.sales.service.impl;

import com.shreespark.pos_api.common.enums.KhataEntryType;
import com.shreespark.pos_api.common.enums.PaymentMethod;
import com.shreespark.pos_api.common.enums.SaleStatus;
import com.shreespark.pos_api.common.enums.StockMovementType;
import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.customer.entity.Customer;
import com.shreespark.pos_api.customer.repository.CustomerRepository;
import com.shreespark.pos_api.gst.entity.GstRate;
import com.shreespark.pos_api.inventory.repository.StockLedgerRepository;
import com.shreespark.pos_api.inventory.service.InventoryService;
import com.shreespark.pos_api.khata.service.KhataService;
import com.shreespark.pos_api.membership.repository.MembershipSubscriptionRepository;
import com.shreespark.pos_api.product.entity.Product;
import com.shreespark.pos_api.product.repository.ProductRepository;
import com.shreespark.pos_api.sales.dto.request.CreateSaleRequest;
import com.shreespark.pos_api.sales.dto.request.SaleItemRequest;
import com.shreespark.pos_api.sales.dto.request.UpdateSaleRequest;
import com.shreespark.pos_api.sales.dto.response.SaleAuditLogResponse;
import com.shreespark.pos_api.sales.dto.response.SaleResponse;
import com.shreespark.pos_api.sales.entity.Sale;
import com.shreespark.pos_api.sales.entity.SaleAuditLog;
import com.shreespark.pos_api.sales.entity.SaleItem;
import com.shreespark.pos_api.sales.mapper.SaleMapper;
import com.shreespark.pos_api.sales.repository.SaleAuditLogRepository;
import com.shreespark.pos_api.sales.repository.SaleRepository;
import com.shreespark.pos_api.sales.service.SaleService;
import com.shreespark.pos_api.staff.entity.Staff;
import com.shreespark.pos_api.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final StockLedgerRepository stockLedgerRepository;
    private final MembershipSubscriptionRepository subscriptionRepository;
    private final StaffRepository staffRepository;
    private final SaleAuditLogRepository saleAuditLogRepository;
    private final InventoryService inventoryService;
    private final KhataService khataService;
    private final SaleMapper saleMapper;

    @Override
    @Transactional
    public SaleResponse create(UUID tenantId, UUID staffId, CreateSaleRequest req) {

        // 1. Resolve staff details
        String staffName = "System Staff";
        if (staffId != null) {
            staffName = staffRepository.findByIdAndTenantIdAndActiveTrue(staffId, tenantId)
                    .map(Staff::getName)
                    .orElse("Staff #" + staffId.toString().substring(0, 8));
        }

        // 2. Resolve customer
        Customer customer = null;
        if (req.customerId() != null) {
            customer = customerRepository.findByIdAndTenantIdAndActiveTrue(req.customerId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", req.customerId()));
        }

        // 3. Resolve membership discount
        BigDecimal membershipDiscount = BigDecimal.ZERO;
        if (customer != null) {
            membershipDiscount = subscriptionRepository
                    .findActiveByCustomerId(customer.getId(), LocalDate.now())
                    .map(ms -> ms.getMembership().getDiscountPercent())
                    .orElse(BigDecimal.ZERO);
        }

        // 4. Validate stock and build line items
        List<SaleItem> items = new ArrayList<>();
        BigDecimal subtotal      = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalCgst     = BigDecimal.ZERO;
        BigDecimal totalSgst     = BigDecimal.ZERO;
        BigDecimal totalIgst     = BigDecimal.ZERO;

        for (SaleItemRequest itemReq : req.items()) {
            Product product = productRepository.findByIdAndTenantIdAndActiveTrue(itemReq.productId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemReq.productId()));

            // validate stock
            int currentStock = stockLedgerRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("StockLedger", product.getId()))
                    .getCurrentStock();

            if (currentStock < itemReq.quantity()) {
                throw new RuntimeException("Insufficient stock for: " + product.getName()
                        + " (available: " + currentStock + ")");
            }

            // pick price based on customer type
            BigDecimal unitPrice = resolvePrice(product, customer);

            // apply membership discount
            BigDecimal discountPct = membershipDiscount;
            BigDecimal discountAmt = unitPrice
                    .multiply(BigDecimal.valueOf(itemReq.quantity()))
                    .multiply(discountPct)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            BigDecimal lineSubtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.quantity()));
            BigDecimal taxableAmt   = lineSubtotal.subtract(discountAmt);

            // Effective GST (Product override -> Category default -> None/0%)
            GstRate gst = product.getGstRate() != null
                    ? product.getGstRate()
                    : (product.getCategory() != null ? product.getCategory().getGstRate() : null);

            BigDecimal cgstPct = BigDecimal.ZERO, sgstPct = BigDecimal.ZERO, igstPct = BigDecimal.ZERO;
            BigDecimal cgstAmt = BigDecimal.ZERO, sgstAmt = BigDecimal.ZERO, igstAmt = BigDecimal.ZERO;

            if (gst != null) {
                if (req.interState()) {
                    igstPct = gst.getIgstRate();
                    igstAmt = taxableAmt.multiply(igstPct).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                } else {
                    cgstPct = gst.getCgstRate();
                    sgstPct = gst.getSgstRate();
                    cgstAmt = taxableAmt.multiply(cgstPct).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    sgstAmt = taxableAmt.multiply(sgstPct).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                }
            }

            BigDecimal lineTotal = taxableAmt.add(cgstAmt).add(sgstAmt).add(igstAmt);

            // Effective HSN (GST Slab -> Product override -> Category default -> None)
            String hsnCode = (gst != null && gst.getHsnCode() != null && !gst.getHsnCode().isBlank())
                    ? gst.getHsnCode()
                    : (product.getHsnCode() != null
                    ? product.getHsnCode()
                    : (product.getCategory() != null ? product.getCategory().getHsnCode() : null));

            SaleItem item = SaleItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .hsnCode(hsnCode)
                    .quantity(itemReq.quantity())
                    .unitPrice(unitPrice)
                    .discountPercent(discountPct)
                    .discountAmount(discountAmt)
                    .taxableAmount(taxableAmt)
                    .cgstPercent(cgstPct).cgstAmount(cgstAmt)
                    .sgstPercent(sgstPct).sgstAmount(sgstAmt)
                    .igstPercent(igstPct).igstAmount(igstAmt)
                    .lineTotal(lineTotal)
                    .build();
            item.setTenantId(tenantId);
            items.add(item);

            subtotal      = subtotal.add(lineSubtotal);
            totalDiscount = totalDiscount.add(discountAmt);
            totalCgst     = totalCgst.add(cgstAmt);
            totalSgst     = totalSgst.add(sgstAmt);
            totalIgst     = totalIgst.add(igstAmt);
        }

        BigDecimal taxableAmount = subtotal.subtract(totalDiscount);
        BigDecimal totalAmount   = taxableAmount.add(totalCgst).add(totalSgst).add(totalIgst);

        // 5. Compute payment split
        BigDecimal paidAmount  = BigDecimal.ZERO;
        BigDecimal khataAmount = BigDecimal.ZERO;

        if (req.paymentMethod() == PaymentMethod.KHATA) {
            khataAmount = totalAmount;
        } else if (req.paymentMethod() == PaymentMethod.SPLIT) {
            paidAmount  = safe(req.cashAmount()).add(safe(req.upiAmount())).add(safe(req.cardAmount()));
            khataAmount = totalAmount.subtract(paidAmount).max(BigDecimal.ZERO);
        } else {
            paidAmount = totalAmount;
        }

        // 6. Validate credit limit for khata
        if (khataAmount.compareTo(BigDecimal.ZERO) > 0 && customer != null) {
            BigDecimal newBalance = customer.getOutstandingBalance().add(khataAmount);
            if (customer.getCreditLimit().compareTo(BigDecimal.ZERO) > 0
                    && newBalance.compareTo(customer.getCreditLimit()) > 0) {
                throw new RuntimeException("Credit limit exceeded for customer: " + customer.getName());
            }
        }

        // 7. Generate invoice number
        String invoiceNumber = generateInvoiceNumber(tenantId);

        // 8. Build and save sale
        Sale sale = Sale.builder()
                .invoiceNumber(invoiceNumber)
                .customerId(customer != null ? customer.getId() : null)
                .customerName(customer != null ? customer.getName() : "Walk-in")
                .staffId(staffId)
                .staffName(staffName)
                .status(SaleStatus.COMPLETED)
                .paymentMethod(req.paymentMethod())
                .subtotal(subtotal)
                .discountAmount(totalDiscount)
                .taxableAmount(taxableAmount)
                .cgstAmount(totalCgst)
                .sgstAmount(totalSgst)
                .igstAmount(totalIgst)
                .totalAmount(totalAmount)
                .paidAmount(paidAmount)
                .khataAmount(khataAmount)
                .interState(req.interState())
                .note(req.note())
                .build();
        sale.setTenantId(tenantId);

        items.forEach(item -> item.setSale(sale));
        sale.setItems(items);
        Sale saved = saleRepository.save(sale);

        // 9. Deduct stock for each item
        for (SaleItem item : items) {
            inventoryService.recordMovement(tenantId, item.getProductId(),
                    StockMovementType.SALE, item.getQuantity(),
                    saved.getId().toString(), "Sale: " + saved.getInvoiceNumber());
        }

        // 10. Update khata if credit sale
        if (khataAmount.compareTo(BigDecimal.ZERO) > 0 && customer != null) {
            khataService.recordEntry(tenantId, customer.getId(),
                    KhataEntryType.DEBIT, khataAmount, saved.getId(), "Credit sale: " + invoiceNumber);
        }

        // 11. Record CREATED Audit Log
        recordAuditLog(tenantId, saved.getId(), "CREATED", staffId, staffName,
                "Bill created for " + saved.getCustomerName() + " — Total: ₹" + saved.getTotalAmount() + " via " + saved.getPaymentMethod());

        List<SaleAuditLog> logs = saleAuditLogRepository.findBySaleIdAndTenantIdOrderByCreatedAtDesc(saved.getId(), tenantId);
        return saleMapper.toResponse(saved, logs);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getById(UUID tenantId, UUID saleId) {
        Sale sale = saleRepository.findByIdAndTenantId(saleId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", saleId));
        List<SaleAuditLog> logs = saleAuditLogRepository.findBySaleIdAndTenantIdOrderByCreatedAtDesc(saleId, tenantId);
        return saleMapper.toResponse(sale, logs);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getByInvoiceNumber(UUID tenantId, String invoiceNumber) {
        Sale sale = saleRepository.findByInvoiceNumberAndTenantId(invoiceNumber, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", invoiceNumber));
        List<SaleAuditLog> logs = saleAuditLogRepository.findBySaleIdAndTenantIdOrderByCreatedAtDesc(sale.getId(), tenantId);
        return saleMapper.toResponse(sale, logs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getAll(UUID tenantId) {
        return saleRepository.findAllByTenantIdOrderByCreatedAtDesc(tenantId)
                .stream().map(s -> {
                    List<SaleAuditLog> logs = saleAuditLogRepository.findBySaleIdAndTenantIdOrderByCreatedAtDesc(s.getId(), tenantId);
                    return saleMapper.toResponse(s, logs);
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getByCustomer(UUID tenantId, UUID customerId) {
        return saleRepository.findAllByTenantIdAndCustomerIdOrderByCreatedAtDesc(tenantId, customerId)
                .stream().map(s -> {
                    List<SaleAuditLog> logs = saleAuditLogRepository.findBySaleIdAndTenantIdOrderByCreatedAtDesc(s.getId(), tenantId);
                    return saleMapper.toResponse(s, logs);
                }).toList();
    }

    @Override
    @Transactional
    public SaleResponse update(UUID tenantId, UUID staffId, UUID saleId, UpdateSaleRequest req) {
        Sale sale = saleRepository.findByIdAndTenantId(saleId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", saleId));

        String staffName = "System Staff";
        if (staffId != null) {
            staffName = staffRepository.findByIdAndTenantIdAndActiveTrue(staffId, tenantId)
                    .map(Staff::getName)
                    .orElse("Staff #" + staffId.toString().substring(0, 8));
        }

        StringBuilder changes = new StringBuilder("Bill updated: ");

        if (req.paymentMethod() != null && !req.paymentMethod().isBlank()) {
            PaymentMethod newPm = PaymentMethod.valueOf(req.paymentMethod());
            if (sale.getPaymentMethod() != newPm) {
                changes.append("Payment Method from ").append(sale.getPaymentMethod()).append(" to ").append(newPm).append("; ");
                sale.setPaymentMethod(newPm);
            }
        }

        if (req.status() != null && !req.status().isBlank()) {
            SaleStatus newStatus = SaleStatus.valueOf(req.status());
            if (sale.getStatus() != newStatus) {
                changes.append("Status from ").append(sale.getStatus()).append(" to ").append(newStatus).append("; ");
                if (newStatus == SaleStatus.CANCELLED) {
                    cancelSaleOperations(tenantId, sale);
                }
                sale.setStatus(newStatus);
            }
        }

        if (req.note() != null) {
            sale.setNote(req.note());
        }

        if (req.editReason() != null && !req.editReason().isBlank()) {
            changes.append("Reason: ").append(req.editReason());
        }

        sale.setEditedByStaffId(staffId);
        sale.setEditedByStaffName(staffName);

        Sale updated = saleRepository.save(sale);

        recordAuditLog(tenantId, updated.getId(), "EDITED", staffId, staffName, changes.toString());

        List<SaleAuditLog> logs = saleAuditLogRepository.findBySaleIdAndTenantIdOrderByCreatedAtDesc(updated.getId(), tenantId);
        return saleMapper.toResponse(updated, logs);
    }

    @Override
    @Transactional
    public SaleResponse cancel(UUID tenantId, UUID saleId) {
        return cancel(tenantId, null, saleId);
    }

    @Override
    @Transactional
    public SaleResponse cancel(UUID tenantId, UUID staffId, UUID saleId) {
        Sale sale = saleRepository.findByIdAndTenantId(saleId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", saleId));

        if (sale.getStatus() == SaleStatus.CANCELLED) {
            throw new RuntimeException("Sale already cancelled");
        }

        String staffName = "System Staff";
        if (staffId != null) {
            staffName = staffRepository.findByIdAndTenantIdAndActiveTrue(staffId, tenantId)
                    .map(Staff::getName)
                    .orElse("Staff #" + staffId.toString().substring(0, 8));
        }

        cancelSaleOperations(tenantId, sale);

        sale.setStatus(SaleStatus.CANCELLED);
        sale.setEditedByStaffId(staffId);
        sale.setEditedByStaffName(staffName);

        Sale saved = saleRepository.save(sale);

        recordAuditLog(tenantId, saved.getId(), "CANCELLED", staffId, staffName, "Sale cancelled and stock/khata reversed.");

        List<SaleAuditLog> logs = saleAuditLogRepository.findBySaleIdAndTenantIdOrderByCreatedAtDesc(saved.getId(), tenantId);
        return saleMapper.toResponse(saved, logs);
    }

    @Override
    @Transactional
    public SaleResponse logPrint(UUID tenantId, UUID staffId, UUID saleId, String printType) {
        Sale sale = saleRepository.findByIdAndTenantId(saleId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", saleId));

        String staffName = "System Staff";
        if (staffId != null) {
            staffName = staffRepository.findByIdAndTenantIdAndActiveTrue(staffId, tenantId)
                    .map(Staff::getName)
                    .orElse("Staff #" + staffId.toString().substring(0, 8));
        }

        String action = "PRINTED".equalsIgnoreCase(printType) || "CASH_MEMO".equalsIgnoreCase(printType)
                ? "CASH_MEMO_PRINTED" : "PRINTED";

        recordAuditLog(tenantId, sale.getId(), action, staffId, staffName, "Printed " + (printType != null ? printType : "Receipt"));

        List<SaleAuditLog> logs = saleAuditLogRepository.findBySaleIdAndTenantIdOrderByCreatedAtDesc(sale.getId(), tenantId);
        return saleMapper.toResponse(sale, logs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleAuditLogResponse> getAllAuditLogs(UUID tenantId) {
        return saleAuditLogRepository.findAllByTenantIdOrderByCreatedAtDesc(tenantId)
                .stream().map(saleMapper::toAuditLogResponse).toList();
    }

    private void cancelSaleOperations(UUID tenantId, Sale sale) {
        // reverse stock
        for (SaleItem item : sale.getItems()) {
            inventoryService.recordMovement(tenantId, item.getProductId(),
                    StockMovementType.RETURN, item.getQuantity(),
                    sale.getId().toString(), "Cancelled: " + sale.getInvoiceNumber());
        }

        // reverse khata if credit sale
        if (sale.getKhataAmount().compareTo(BigDecimal.ZERO) > 0 && sale.getCustomerId() != null) {
            khataService.recordEntry(tenantId, sale.getCustomerId(),
                    KhataEntryType.CREDIT, sale.getKhataAmount(),
                    sale.getId(), "Cancelled sale: " + sale.getInvoiceNumber());
        }
    }

    private void recordAuditLog(UUID tenantId, UUID saleId, String action, UUID staffId, String staffName, String details) {
        SaleAuditLog log = SaleAuditLog.builder()
                .saleId(saleId)
                .action(action)
                .staffId(staffId)
                .staffName(staffName)
                .details(details)
                .build();
        log.setTenantId(tenantId);
        saleAuditLogRepository.save(log);
    }

    private BigDecimal resolvePrice(Product product, Customer customer) {
        if (customer == null) return product.getRetailPrice();
        return switch (customer.getType()) {
            case WHOLESALE -> product.getWholesalePrice() != null
                    ? product.getWholesalePrice() : product.getRetailPrice();
            case DEALER    -> product.getDealerPrice() != null
                    ? product.getDealerPrice() : product.getRetailPrice();
            default        -> product.getRetailPrice();
        };
    }

    private String generateInvoiceNumber(UUID tenantId) {
        int seq = saleRepository.findMaxInvoiceSequence(tenantId) + 1;
        return "INV-" + String.format("%06d", seq);
    }

    private BigDecimal safe(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }
}

