-- Network Device Inventory & Monitoring System - Database Schema
-- Run this once before starting the app:  mysql -u root -p < schema.sql

CREATE DATABASE IF NOT EXISTS network_inventory_db;
USE network_inventory_db;

CREATE TABLE IF NOT EXISTS devices (
    device_id     INT AUTO_INCREMENT PRIMARY KEY,
    device_name   VARCHAR(100) NOT NULL,
    device_type   ENUM('ROUTER', 'SWITCH', 'FIREWALL', 'ACCESS_POINT', 'SERVER', 'OTHER') NOT NULL,
    ip_address    VARCHAR(45)  NOT NULL UNIQUE,   -- IPv4 or IPv6
    location      VARCHAR(100),
    status        ENUM('UP', 'DOWN', 'MAINTENANCE') NOT NULL DEFAULT 'UP',
    vendor        VARCHAR(50),
    installed_on  DATE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Every maintenance event (repair, firmware upgrade, inspection) for a device.
CREATE TABLE IF NOT EXISTS maintenance_logs (
    log_id        INT AUTO_INCREMENT PRIMARY KEY,
    device_id     INT NOT NULL,
    description   VARCHAR(255) NOT NULL,
    performed_by  VARCHAR(100),
    performed_on  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_maintenance_device
        FOREIGN KEY (device_id) REFERENCES devices(device_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_devices_type ON devices(device_type);
CREATE INDEX idx_devices_status ON devices(status);
CREATE INDEX idx_maintenance_device ON maintenance_logs(device_id);
