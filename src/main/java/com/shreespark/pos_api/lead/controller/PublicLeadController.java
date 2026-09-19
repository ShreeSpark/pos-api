package com.shreespark.pos_api.lead.controller;

import com.shreespark.pos_api.lead.entity.LeadQuery;
import com.shreespark.pos_api.lead.repository.LeadQueryRepository;
import com.shreespark.pos_api.subscription.dto.response.SubscriptionPlanConfigResponse;
import com.shreespark.pos_api.subscription.service.SubscriptionPlanConfigService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicLeadController {

    private final LeadQueryRepository leadQueryRepository;
    private final SubscriptionPlanConfigService planConfigService;

    @Data
    public static class CreateLeadRequest {
        private String name;
        private String email;
        private String phone;
        private String businessName;
        private String city;
        private String planName;
        private String message;
    }

    @Data
    public static class CreateContactRequest {
        private String name;
        private String email;
        private String phone;
        private String subject;
        private String message;
    }

    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlanConfigResponse>> getPublicPlans() {
        return ResponseEntity.ok(planConfigService.getAll());
    }

    @PostMapping("/leads")
    public ResponseEntity<?> submitLead(@RequestBody CreateLeadRequest req) {
        LeadQuery lead = LeadQuery.builder()
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .businessName(req.getBusinessName())
                .city(req.getCity())
                .planName(req.getPlanName() != null ? req.getPlanName() : "General Inquiry")
                .queryType("LEAD")
                .message(req.getMessage())
                .status("NEW")
                .build();

        LeadQuery saved = leadQueryRepository.save(lead);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Your inquiry has been submitted successfully! Our team will contact you shortly.",
                "leadId", saved.getId()
        ));
    }

    @PostMapping("/contact")
    public ResponseEntity<?> submitContact(@RequestBody CreateContactRequest req) {
        LeadQuery contact = LeadQuery.builder()
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .planName(req.getSubject())
                .queryType("CONTACT")
                .message(req.getMessage())
                .status("NEW")
                .build();

        LeadQuery saved = leadQueryRepository.save(contact);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Thank you for reaching out! We have received your message and will respond soon.",
                "contactId", saved.getId()
        ));
    }
}
