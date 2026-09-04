package com.shreespark.pos_api.gst.repository;

import com.shreespark.pos_api.gst.entity.GstRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GstRateRepository extends JpaRepository<GstRate, UUID> {
    List<GstRate> findAllByActiveTrue();
    Optional<GstRate> findByIdAndActiveTrue(UUID id);
    boolean existsByRate(BigDecimal rate);
}
