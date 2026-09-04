package com.shreespark.pos_api.membership.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignMembershipRequest(
        @NotNull UUID membershipId
) {}
