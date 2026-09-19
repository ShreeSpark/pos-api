package com.shreespark.pos_api.lead.repository;

import com.shreespark.pos_api.lead.entity.LeadQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadQueryRepository extends JpaRepository<LeadQuery, Long> {

    Page<LeadQuery> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    Page<LeadQuery> findByQueryTypeOrderByCreatedAtDesc(String queryType, Pageable pageable);

    Page<LeadQuery> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByStatus(String status);
}
