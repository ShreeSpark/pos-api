package com.shreespark.pos_api.permission.entity;

import com.shreespark.pos_api.common.enums.Permission;
import com.shreespark.pos_api.common.enums.Role;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public final class DefaultRolePermissions {

    private DefaultRolePermissions() {}

    private static final Map<Role, Set<Permission>> DEFAULTS = new EnumMap<>(Role.class);

    static {
        DEFAULTS.put(Role.ADMIN, Set.of(Permission.values()));

        DEFAULTS.put(Role.MANAGER, Set.of(
                Permission.BILLING_CREATE,
                Permission.BILLING_VIEW_ALL,
                Permission.BILLING_APPLY_DISCOUNT,
                Permission.PRODUCTS_CREATE,
                Permission.PRODUCTS_EDIT,
                Permission.BARCODE_GENERATE,
                Permission.CUSTOMERS_VIEW,
                Permission.CUSTOMERS_EDIT,
                Permission.KHATA_VIEW,
                Permission.KHATA_RECORD_PAYMENT,
                Permission.KHATA_MANUAL_ENTRY,
                Permission.REPORTS_VIEW,
                Permission.REMINDERS_SEND,
                Permission.STAFF_MANAGE
        ));

        DEFAULTS.put(Role.CASHIER, Set.of(
                Permission.BILLING_CREATE,
                Permission.BILLING_APPLY_DISCOUNT,
                Permission.CUSTOMERS_VIEW,
                Permission.KHATA_VIEW,
                Permission.KHATA_RECORD_PAYMENT
        ));

        DEFAULTS.put(Role.WAREHOUSE, Set.of(
                Permission.PRODUCTS_CREATE,
                Permission.PRODUCTS_EDIT,
                Permission.BARCODE_GENERATE
        ));

        DEFAULTS.put(Role.SUPER_ADMIN, Set.of(Permission.values()));
    }

    public static Set<Permission> forRole(Role role) {
        return DEFAULTS.getOrDefault(role, Set.of());
    }
}
