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
import com.shreespark.pos_api.sales.dto.response.SaleResponse;
import com.shreespark.pos_api.sales.entity.Sale;
import com.shreespark.pos_api.sales.entity.SaleItem;
import com.shreespark.pos_api.sales.mapper.SaleMapper;
import com.shreespark.pos_api.sales.repository.SaleRepository;
import com.shreespark.pos_api.sales.service.SaleService;
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
    private final InventoryService inventoryService;
    private final KhataService khataService;
    private final SaleMapper saleMapper;

    @Override
    @Transactional
    public SaleResponse create(UUID tenantId, UUID staffId, CreateSaleRequest req) {

        // 1. Resolve customer
        Customer customer = null;
        if (req.customerId() != null) {
            customer = customerRepository.findByIdAndTenantIdAndActiveTrue(req.customerId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", req.customerId()));
        }

        // 2. Resolve membership discount
        BigDecimal membershipDiscount = BigDecimal.ZERO;
        if (customer != null) {
            membershipDiscount = subscriptionRepository
                    .findActiveByCustomerId(customer.getId(), LocalDate.now())
                    .map(ms -> ms.getMembership().getDiscountPercent())
                    .orElse(BigDecimal.ZERO);
        }

        // 3. Validate stock and build line items
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

            // GST from category
            GstRate gst = product.getCategory() != null ? product.getCategory().getGstRate() : null;
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

            String hsnCode = product.getCategory() != null ? product.getCategory().getHsnCode() : null;

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

        // 4. Compute payment split
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

        // 5. Validate credit limit for khata
        if (khataAmount.compareTo(BigDecimal.ZERO) > 0 && customer != null) {
            BigDecimal newBalance = customer.getOutstandingBalance().add(khataAmount);
            if (customer.getCreditLimit().compareTo(BigDecimal.ZERO) > 0
                    && newBalance.compareTo(customer.getCreditLimit()) > 0) {
                throw new RuntimeException("Credit limit exceeded for customer: " + customer.getName());
            }
        }

        // 6. Generate invoice number
        String invoiceNumber = generateInvoiceNumber(tenantId);

        // 7. Build and save sale
        Sale sale = Sale.builder()
                .invoiceNumber(invoiceNumber)
                .customerId(customer != null ? customer.getId() : null)
                .customerName(customer != null ? customer.getName() : "Walk-in")
                .staffId(staffId)
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

        // 8. Deduct stock for each item
        for (SaleItem item : items) {
            inventoryService.recordMovement(tenantId, item.getProductId(),
                    StockMovementType.SALE, item.getQuantity(),
                    saved.getId().toString(), "Sale: " + saved.getInvoiceNumber());
        }

        // 9. Update khata if credit sale
        if (khataAmount.compareTo(BigDecimal.ZERO) > 0 && customer != null) {
            khataService.recordEntry(tenantId, customer.getId(),
                    KhataEntryType.DEBIT, khataAmount, saved.getId(), "Credit sale: " + invoiceNumber);
        }

        return saleMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getById(UUID tenantId, UUID saleId) {
        return saleMapper.toResponse(
                saleRepository.findByIdAndTenantId(saleId, tenantId)
                        .orElseThrow(() -> new ResourceNotFoundException("Sale", saleId)));
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getByInvoiceNumber(UUID tenantId, String invoiceNumber) {
        return saleMapper.toResponse(
                saleRepository.findByInvoiceNumberAndTenantId(invoiceNumber, tenantId)
                        .orElseThrow(() -> new ResourceNotFoundException("Sale", invoiceNumber)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getAll(UUID tenantId) {
        return saleRepository.findAllByTenantIdOrderByCreatedAtDesc(tenantId)
                .stream().map(saleMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getByCustomer(UUID tenantId, UUID customerId) {
        return saleRepository.findAllByTenantIdAndCustomerIdOrderByCreatedAtDesc(tenantId, customerId)
                .stream().map(saleMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public SaleResponse cancel(UUID tenantId, UUID saleId) {
        Sale sale = saleRepository.findByIdAndTenantId(saleId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", saleId));

        if (sale.getStatus() == SaleStatus.CANCELLED) {
            throw new RuntimeException("Sale already cancelled");
        }

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

        sale.setStatus(SaleStatus.CANCELLED);
        return saleMapper.toResponse(saleRepository.save(sale));
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
