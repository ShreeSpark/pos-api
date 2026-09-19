package com.shreespark.pos_api.release.repository;

import com.shreespark.pos_api.release.entity.AppRelease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppReleaseRepository extends JpaRepository<AppRelease, UUID> {
    List<AppRelease> findAllByTargetAppAndActiveTrueOrderByVersionCodeDesc(String targetApp);
    Optional<AppRelease> findFirstByTargetAppAndActiveTrueOrderByVersionCodeDesc(String targetApp);
    List<AppRelease> findAllByActiveTrueOrderByCreatedAtDesc();
}
