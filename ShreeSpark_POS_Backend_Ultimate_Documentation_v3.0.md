
# ShreeSpark POS Billing Platform - Ultimate Backend Documentation (v3.0)

> Production-ready Spring Boot SaaS POS Billing Platform

## Overview

This document merges both backend guides into a single developer documentation.

### Technology Stack

- Java 21 (LTS)
- Spring Boot 4.1.x
- Maven
- PostgreSQL 16
- Flyway
- Spring Security + JWT
- Swagger / OpenAPI
- Electron + React Desktop (Offline)
- SQLite Offline Cache

---

## Architecture

ShreeSpark is a **Multi-Tenant SaaS POS Billing Platform**.

### Major Modules

1. Authentication & JWT
2. Tenant Management
3. Staff & Roles
4. Subscription & Licensing
5. Device Activation
6. Product & Barcode
7. Inventory
8. Customer & Membership
9. Khata Ledger
10. Sales & Billing
11. Payment
12. Reports
13. Reminder
14. Offline Sync
15. Super Admin Platform

---

## Spring Initializr

| Field | Value |
|-------|-------|
| Project | Maven |
| Language | Java |
| Spring Boot | 4.1.1 |
| Group | com.shreespark |
| Artifact | shreespark-pos-api |
| Package | com.shreespark.pos |
| Packaging | Jar |
| Java | 21 |
| Config | YAML |

### Dependencies

- Spring Web
- Spring Data JPA
- Spring Security
- Validation
- PostgreSQL Driver
- Lombok
- Flyway
- OpenAPI Swagger
- Spring Boot Actuator
- DevTools

---

## Folder Structure

```text
com.shreespark.pos
├── config
├── common
├── auth
├── tenant
├── platform
├── staff
├── permission
├── subscription
├── device
├── product
├── inventory
├── customer
├── membership
├── khata
├── sales
├── payment
├── reminder
├── reports
├── sync
└── notification
```

---

## Multi-Tenant Database

Every table contains:

- id
- tenant_id
- created_at
- created_by
- updated_at
- updated_by
- deleted_at
- deleted_by
- active
- version

### Platform Tables

- tenants
- plans
- subscriptions
- devices
- platform_users

### Business Tables

- staff
- roles
- permissions
- role_permissions
- products
- categories
- brands
- barcodes
- customers
- memberships
- membership_subscriptions
- sales
- sale_items
- payments
- payment_transactions
- khata_entries
- reminder_logs
- stock_movements
- stock_adjustments
- sync_logs
- sync_queue
- activity_logs

---

## Authentication

### Tenant Login

`POST /api/tenant/auth/login`

Returns:

- Access Token
- Refresh Token
- Staff Profile
- Permissions
- Tenant Information
- Subscription Information

### JWT Claims

```json
{
  "staffId":"UUID",
  "tenantId":"UUID",
  "role":"ADMIN",
  "permissions":[
    "BILLING_CREATE",
    "PRODUCTS_EDIT",
    "REPORTS_VIEW"
  ]
}
```

---

## Roles & Permissions

### Roles

- ADMIN
- MANAGER
- CASHIER
- WAREHOUSE
- SUPER_ADMIN

### Permission Categories

- BILLING_CREATE
- BILLING_VIEW_ALL
- BILLING_APPLY_DISCOUNT
- PRODUCTS_CREATE
- PRODUCTS_EDIT
- PRODUCTS_DELETE
- BARCODE_GENERATE
- CUSTOMERS_VIEW
- CUSTOMERS_EDIT
- KHATA_VIEW
- KHATA_RECORD_PAYMENT
- KHATA_MANUAL_ENTRY
- REPORTS_VIEW
- REMINDERS_SEND
- STAFF_MANAGE
- DEVICE_MANAGE
- SETTINGS_MANAGE

---

## Subscription & Licensing

### Plans

| Plan | Devices | Admins | Staff |
|------|---------|--------|------|
| Starter | 1 | 1 | 0 |
| Growth | 2 | 1 | 2 |
| Business | 4 | 2 | 5 |
| Enterprise | Unlimited | Unlimited | Unlimited |

### Offline License

- Online Login generates signed License JWT.
- Offline Billing allowed for 7 days.
- Grace Period: 7 additional days.
- After grace period: New Billing disabled until reconnect.

---

## Product Module

### APIs

- GET /api/products
- POST /api/products
- PUT /api/products/{id}
- DELETE /api/products/{id}
- GET /api/products/lookup/{barcode}
- POST /api/products/{id}/barcode/generate

### Features

- Multiple Barcodes
- QR / CODE128 / EAN13
- MOQ
- Low Stock Threshold

---

## Inventory Module

Stock movement types:

- PURCHASE
- SALE
- RETURN
- DAMAGE
- ADJUSTMENT

Inventory updates automatically after every billing transaction.

---

## Customer Module

Customer Types:

- Retail
- Wholesale

Membership Tiers:

- Silver
- Gold
- Diamond

Features:

- Credit Limit
- Outstanding Balance
- Membership Discounts

---

## Khata Ledger Module

Entry Types:

- DEBIT
- CREDIT

Rules:

- Every payment updates customer outstanding balance.
- Every sale on credit creates Khata Entry.
- Transactional update using @Transactional.

APIs:

- GET /api/customers/{id}/khata
- POST /api/customers/{id}/khata/entries
- POST /api/customers/{id}/khata/payments

---

## Sales Module

Flow:

1. Scan Barcode
2. Validate Stock
3. Create Sale
4. Create Sale Items
5. Deduct Stock
6. Process Payment
7. Update Khata
8. Generate Invoice

---

## Payment Module

Supported:

- Cash
- UPI
- Card
- Khata
- Split Payment

Webhook:

`POST /api/payments/upi/webhook`

---

## Reminder Module

Channels:

- WhatsApp
- SMS

Scheduler sends reminders for overdue Khata customers.

APIs:

- GET /api/reminders/overdue
- POST /api/customers/{id}/reminders/send
- POST /api/reminders/send-bulk

---

## Offline Sync

### Pull

`GET /api/sync/pull`

### Push

`POST /api/sync/push`

Conflict Resolution:

- Negative Stock → Reject
- Negative Khata → Reject
- Timestamp Conflict → Last Write Wins

---

## Super Admin Platform

Base URL:

`/api/platform`

Modules:

- Dashboard
- Tenants
- Plans
- Subscriptions
- Devices
- Analytics

---

## Security

- JWT Authentication
- BCrypt Password Hashing
- Refresh Tokens
- RBAC Permissions
- Tenant Isolation
- Audit Logs
- CORS
- HTTPS

---

## application.yml

```yaml
server:
  port:8080

spring:
  datasource:
    url:
    username:
    password:

  jpa:
    hibernate:
      ddl-auto: validate

  flyway:
    enabled: true
```

---

## Deployment

Production Stack:

- Ubuntu 24.04
- Docker Compose
- PostgreSQL 16
- Nginx Reverse Proxy
- SSL (Let's Encrypt)

---

## Development Roadmap

1. Tenant + Auth
2. Staff + Permissions
3. Product + Barcode
4. Inventory
5. Customer + Membership
6. Sales + Payment + Khata
7. Reminder
8. Offline Sync
9. Subscription & Device
10. Super Admin

---

## Testing Checklist

- Authentication
- Products
- Inventory
- Billing
- Khata
- Offline Sync
- Subscription
- Reports

---

## Production Best Practices

- Flyway Migrations Only
- Soft Delete
- Optimistic Locking
- DTO Validation
- Swagger Documentation
- Audit Trail
- Actuator Monitoring
- PostgreSQL Indexes
- Transaction Management
- Multi-Tenant Query Filtering

---

**Version:** v3.0 Production Documentation

**Audience:** Backend Developers, QA Engineers, DevOps, Technical Leads.
