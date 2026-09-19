package com.shreespark.pos_api.lead.controller;

import com.shreespark.pos_api.lead.entity.LeadQuery;
import com.shreespark.pos_api.lead.repository.LeadQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/platform/leads")
@RequiredArgsConstructor
public class PlatformLeadController {

    private final LeadQueryRepository leadQueryRepository;

    @Data
    public static class UpdateLeadStatusRequest {
        private String status;
        private String adminNotes;
    }

    @GetMapping
    public ResponseEntity<Page<LeadQuery>> getLeads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String queryType
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (status != null && !status.isEmpty()) {
            return ResponseEntity.ok(leadQueryRepository.findByStatusOrderByCreatedAtDesc(status, pageable));
        } else if (queryType != null && !queryType.isEmpty()) {
            return ResponseEntity.ok(leadQueryRepository.findByQueryTypeOrderByCreatedAtDesc(queryType, pageable));
        }
        return ResponseEntity.ok(leadQueryRepository.findAllByOrderByCreatedAtDesc(pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        long newCount = leadQueryRepository.countByStatus("NEW");
        long totalCount = leadQueryRepository.count();
        return ResponseEntity.ok(Map.of(
                "newLeads", newCount,
                "totalLeads", totalCount
        ));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateLeadStatusRequest req
    ) {
        return leadQueryRepository.findById(id).map(lead -> {
            if (req.getStatus() != null) {
                lead.setStatus(req.getStatus());
            }
            if (req.getAdminNotes() != null) {
                lead.setAdminNotes(req.getAdminNotes());
            }
            LeadQuery updated = leadQueryRepository.save(lead);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLead(@PathVariable Long id) {
        if (leadQueryRepository.existsById(id)) {
            leadQueryRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Lead deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }
}
