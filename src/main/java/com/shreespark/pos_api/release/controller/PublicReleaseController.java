package com.shreespark.pos_api.release.controller;

import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.release.dto.ReleaseResponse;
import com.shreespark.pos_api.release.entity.AppRelease;
import com.shreespark.pos_api.release.repository.AppReleaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/releases")
@RequiredArgsConstructor
public class PublicReleaseController {

    private final AppReleaseRepository releaseRepository;

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<ReleaseResponse>> getLatestRelease(
            @RequestParam(defaultValue = "POS_DESKTOP") String targetApp) {
        var releaseOpt = releaseRepository.findFirstByTargetAppAndActiveTrueOrderByVersionCodeDesc(targetApp);
        if (releaseOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(null));
        }
        AppRelease r = releaseOpt.get();
        ReleaseResponse resp = new ReleaseResponse(
                r.getId(),
                r.getTargetApp(),
                r.getVersion(),
                r.getVersionCode(),
                r.getReleaseNotes(),
                r.getDownloadUrl(),
                r.isMandatory(),
                r.isActive(),
                r.getCreatedAt()
        );
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }
}
