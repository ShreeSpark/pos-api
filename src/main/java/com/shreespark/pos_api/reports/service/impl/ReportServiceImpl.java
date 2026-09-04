package com.shreespark.pos_api.reports.service.impl;

import com.shreespark.pos_api.common.enums.SaleStatus;
import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.customer.entity.Customer;
import com.shreespark.pos_api.customer.repository.CustomerRepository;
import com.shreespark.pos_api.inventory.entity.StockLedger;
import com.shreespark.pos_api.inventory.repository.StockLedgerRepository;
import com.shreespark.pos_api.khata.entity.KhataEntry;
import com.shreespark.pos_api.khata.repository.KhataEntryRepository;
import com.shreespark.pos_api.reports.dto.response.CustomerLedgerResponse;
import com.shreespark.pos_api.reports.dto.response.GstReportResponse;
import com.shreespark.pos_api.reports.dto.response.SalesReportResponse;
import com.shreespark.pos_api.reports.dto.response.StockReportResponse;
import com.shreespark.pos_api.reports.service.ReportService;
import com.shreespark.pos_api.sales.entity.Sale;
import com.shreespark.pos_api.sales.entity.SaleItem;
import com.shreespark.pos_api.sales.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final SaleRepository saleRepository;
    private final StockLedgerRepository stockLedgerRepository;
    private final CustomerRepository customerRepository;
    private final KhataEntryRepository khataEntryRepository;

    @Override
    @Transactional(readOnly = true)
    public SalesReportResponse salesReport(UUID tenantId, LocalDate from, LocalDate to) {
        List<Sale> sales = saleRepository.findByTenantIdAndDateRange(
                tenantId,
                from.atStartOfDay().toInstant(ZoneOffset.UTC),
                to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));

        List<Sale> completed = sales.stream()
                .filter(s -> s.getStatus() == SaleStatus.COMPLETED).toList();
        int cancelled = (int) sales.stream()
                .filter(s -> s.getStatus() == SaleStatus.CANCELLED).count();

        BigDecimal revenue  = sum(completed, Sale::getTotalAmount);
        BigDecimal discount = sum(completed, Sale::getDiscountAmount);
        BigDecimal cgst     = sum(completed, Sale::getCgstAmount);
        BigDecimal sgst     = sum(completed, Sale::getSgstAmount);
        BigDecimal igst     = sum(completed, Sale::getIgstAmount);
        BigDecimal tax      = cgst.add(sgst).add(igst);
        BigDecimal net      = revenue.subtract(tax);

        // daily breakdown
        Map<String, List<Sale>> byDay = completed.stream().collect(
                Collectors.groupingBy(s -> s.getCreatedAt()
                        .atZone(ZoneOffset.UTC).toLocalDate().toString()));

        List<SalesReportResponse.DailySalesSummary> daily = byDay.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new SalesReportResponse.DailySalesSummary(
                        e.getKey(),
                        e.getValue().size(),
                        sum(e.getValue(), Sale::getTotalAmount),
                        sum(e.getValue(), s -> s.getCgstAmount().add(s.getSgstAmount()).add(s.getIgstAmount()))
                )).toList();

        return new SalesReportResponse(completed.size(), cancelled,
                revenue, discount, cgst, sgst, igst, tax, net, daily);
    }

    @Override
    @Transactional(readOnly = true)
    public StockReportResponse stockReport(UUID tenantId) {
        List<StockLedger> ledgers = stockLedgerRepository.findAllByTenantId(tenantId);

        List<StockReportResponse.StockItem> items = ledgers.stream().map(sl -> {
            var p = sl.getProduct();
            int threshold = p.getLowStockThreshold() != null ? p.getLowStockThreshold() : 0;
            boolean low = sl.getCurrentStock() <= threshold && sl.getCurrentStock() > 0;
            boolean out = sl.getCurrentStock() == 0;
            String cat = p.getCategory() != null ? p.getCategory().getName() : null;
            return new StockReportResponse.StockItem(
                    p.getId().toString(), p.getName(), p.getSku(),
                    cat, sl.getCurrentStock(), threshold, low, out);
        }).toList();

        int lowCount = (int) items.stream().filter(StockReportResponse.StockItem::isLowStock).count();
        int outCount = (int) items.stream().filter(StockReportResponse.StockItem::isOutOfStock).count();

        return new StockReportResponse(items.size(), lowCount, outCount, items);
    }

    @Override
    @Transactional(readOnly = true)
    public GstReportResponse gstReport(UUID tenantId, LocalDate from, LocalDate to) {
        List<Sale> sales = saleRepository.findByTenantIdAndDateRange(
                tenantId,
                from.atStartOfDay().toInstant(ZoneOffset.UTC),
                to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))
                .stream().filter(s -> s.getStatus() == SaleStatus.COMPLETED).toList();

        // group sale items by HSN code
        Map<String, List<SaleItem>> byHsn = new LinkedHashMap<>();
        for (Sale sale : sales) {
            for (SaleItem item : sale.getItems()) {
                String key = item.getHsnCode() != null ? item.getHsnCode() : "NONE";
                byHsn.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
            }
        }

        List<GstReportResponse.GstSlabSummary> slabs = byHsn.entrySet().stream().map(e -> {
            List<SaleItem> its = e.getValue();
            BigDecimal taxable = sumItems(its, SaleItem::getTaxableAmount);
            BigDecimal cgst    = sumItems(its, SaleItem::getCgstAmount);
            BigDecimal sgst    = sumItems(its, SaleItem::getSgstAmount);
            BigDecimal igst    = sumItems(its, SaleItem::getIgstAmount);
            BigDecimal rate    = its.get(0).getCgstPercent().add(its.get(0).getSgstPercent())
                                    .add(its.get(0).getIgstPercent());
            return new GstReportResponse.GstSlabSummary(
                    e.getKey(), null, rate, taxable, cgst, sgst, igst);
        }).toList();

        BigDecimal totalTaxable = slabs.stream().map(GstReportResponse.GstSlabSummary::taxableAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCgst = slabs.stream().map(GstReportResponse.GstSlabSummary::cgstAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalSgst = slabs.stream().map(GstReportResponse.GstSlabSummary::sgstAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalIgst = slabs.stream().map(GstReportResponse.GstSlabSummary::igstAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new GstReportResponse(totalTaxable, totalCgst, totalSgst, totalIgst,
                totalCgst.add(totalSgst).add(totalIgst), slabs);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerLedgerResponse customerLedger(UUID tenantId, UUID customerId) {
        Customer customer = customerRepository.findByIdAndTenantIdAndActiveTrue(customerId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        List<KhataEntry> entries = khataEntryRepository
                .findAllByCustomerIdAndCustomerTenantIdOrderByCreatedAtDesc(customerId, tenantId);

        List<CustomerLedgerResponse.LedgerEntry> ledger = entries.stream().map(e ->
                new CustomerLedgerResponse.LedgerEntry(
                        e.getId().toString(),
                        e.getType().name(),
                        e.getAmount(),
                        e.getBalanceBefore(),
                        e.getBalanceAfter(),
                        e.getNote(),
                        e.getCreatedAt().toString()
                )).toList();

        return new CustomerLedgerResponse(
                customer.getId().toString(), customer.getName(), customer.getPhone(),
                customer.getCreditLimit(), customer.getOutstandingBalance(), ledger);
    }

    private <T> BigDecimal sum(List<T> list, java.util.function.Function<T, BigDecimal> fn) {
        return list.stream().map(fn).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumItems(List<SaleItem> items, java.util.function.Function<SaleItem, BigDecimal> fn) {
        return items.stream().map(fn).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
