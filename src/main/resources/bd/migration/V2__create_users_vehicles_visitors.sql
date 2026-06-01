-- ============================================================
-- feature/users - Migración de base de datos
-- MySQL 8+
-- Tablas: users, vehicles, visitors
-- ============================================================
 
-- ──────────────────────────────────────────────────────────
-- TABLA: users
-- Usuarios del SaaS sincronizados desde el condominio externo
-- ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id                    BIGINT          NOT NULL AUTO_INCREMENT,
    external_id           VARCHAR(100)    NOT NULL COMMENT 'ID del usuario en el sistema del condominio',
    tenant_id             VARCHAR(100)    NOT NULL COMMENT 'ID del condominio (multi-tenant)',
    name                  VARCHAR(100)    NOT NULL,
    email                 VARCHAR(150)    NOT NULL,
    phone_number          VARCHAR(20),
    apartment_number      VARCHAR(20)     COMMENT 'Número de unidad del propietario',
    external_apartment_id VARCHAR(100)    COMMENT 'ID del apartamento en sistema externo',
    role                  VARCHAR(30)     NOT NULL COMMENT 'ADMIN_CONDOMINIO | PROPIETARIO | AGENTE_SEGURIDAD',
    status                VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE | INACTIVE | SUSPENDED',
    created_at            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_synced_at        DATETIME        COMMENT 'Última sincronización con sistema externo',
 
    PRIMARY KEY (id),
    UNIQUE KEY uq_user_external_tenant (external_id, tenant_id),
    INDEX idx_user_tenant (tenant_id),
    INDEX idx_user_role (tenant_id, role),
    INDEX idx_user_status (tenant_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Usuarios del SaaS - sincronizados desde API del condominio';
 
 
-- ──────────────────────────────────────────────────────────
-- TABLA: vehicles
-- Vehículos registrados por usuarios del condominio
-- ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS vehicles (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    tenant_id       VARCHAR(100)    NOT NULL COMMENT 'ID del condominio',
    plate           VARCHAR(20)     NOT NULL COMMENT 'Placa normalizada a mayúsculas',
    brand           VARCHAR(50),
    model           VARCHAR(50),
    color           VARCHAR(30),
    type            VARCHAR(30)     COMMENT 'sedan | suv | moto | camioneta | etc.',
    owner_user_id   BIGINT          NOT NULL COMMENT 'FK al usuario propietario en el SaaS',
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 
    PRIMARY KEY (id),
    UNIQUE KEY uq_vehicle_plate_tenant (plate, tenant_id),
    INDEX idx_vehicle_tenant (tenant_id),
    INDEX idx_vehicle_owner (owner_user_id),
    INDEX idx_vehicle_plate (plate, tenant_id),
    CONSTRAINT fk_vehicle_owner FOREIGN KEY (owner_user_id)
        REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Vehículos registrados por usuarios del condominio';
 
 
-- ──────────────────────────────────────────────────────────
-- TABLA: visitors
-- Visitantes temporales autorizados por propietarios
-- ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS visitors (
    id                      BIGINT          NOT NULL AUTO_INCREMENT,
    tenant_id               VARCHAR(100)    NOT NULL COMMENT 'ID del condominio',
    name                    VARCHAR(100)    NOT NULL,
    id_document             VARCHAR(50)     COMMENT 'DNI / Pasaporte del visitante',
    vehicle_plate           VARCHAR(20)     COMMENT 'Placa del vehículo del visitante (opcional)',
    vehicle_description     VARCHAR(100),
    authorized_by_user_id   BIGINT          NOT NULL COMMENT 'Usuario que autorizó la visita',
    valid_from              DATETIME        NOT NULL COMMENT 'Inicio del período de validez',
    valid_until             DATETIME        NOT NULL COMMENT 'Fin del período de validez',
    is_active               BOOLEAN         NOT NULL DEFAULT TRUE,
    notes                   VARCHAR(255),
    created_at              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 
    PRIMARY KEY (id),
    INDEX idx_visitor_tenant (tenant_id),
    INDEX idx_visitor_authorized_by (authorized_by_user_id),
    INDEX idx_visitor_plate_active (tenant_id, vehicle_plate, is_active),
    INDEX idx_visitor_validity (tenant_id, is_active, valid_from, valid_until),
    CONSTRAINT fk_visitor_authorized_by FOREIGN KEY (authorized_by_user_id)
        REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Visitantes temporales autorizados por propietarios';