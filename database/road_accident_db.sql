-- Run this script in MySQL / phpMyAdmin to create the application's database.
-- The current Java prototype compares passwords as plain text; use only
-- development credentials until password hashing is implemented.

CREATE DATABASE IF NOT EXISTS road_accident_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE road_accident_db;

-- 1. Accounts used by registration, login, and admin approval.
CREATE TABLE IF NOT EXISTS users (
    user_id INT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    zone VARCHAR(50) NOT NULL,
    driving_licence_number VARCHAR(100) DEFAULT NULL,
    ambulance_number VARCHAR(50) DEFAULT NULL,
    hospital_name VARCHAR(150) DEFAULT NULL,
    designation VARCHAR(100) DEFAULT NULL,
    police_station VARCHAR(150) DEFAULT NULL,
    badge_number VARCHAR(50) DEFAULT NULL,
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    UNIQUE KEY uq_users_phone_number (phone_number),
    KEY idx_users_verification_status (verification_status),
    KEY idx_users_role_status (role, verification_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Public accident reports. The app uses the singular table name "accident".
CREATE TABLE IF NOT EXISTS accident (
    accident_id INT NOT NULL AUTO_INCREMENT,
    reporter_name VARCHAR(100) NOT NULL,
    reporter_phone VARCHAR(20) DEFAULT NULL,
    accident_date DATE NOT NULL,
    accident_time TIME NOT NULL,
    location VARCHAR(255) NOT NULL,
    accident_type VARCHAR(100) DEFAULT NULL,
    severity VARCHAR(20) NOT NULL,
    victim_count INT NOT NULL DEFAULT 0,
    description TEXT DEFAULT NULL,
    status VARCHAR(40) NOT NULL DEFAULT 'Reported',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (accident_id),
    KEY idx_accident_location_created (location, created_at),
    KEY idx_accident_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Hospitals and their currently available beds.
CREATE TABLE IF NOT EXISTS hospitals (
    hospital_id INT NOT NULL AUTO_INCREMENT,
    hospital_name VARCHAR(150) NOT NULL,
    zone VARCHAR(50) NOT NULL,
    available_beds INT NOT NULL DEFAULT 0,
    phone_number VARCHAR(20) DEFAULT NULL,
    PRIMARY KEY (hospital_id),
    UNIQUE KEY uq_hospital_name_zone (hospital_name, zone),
    KEY idx_hospitals_zone_beds (zone, available_beds)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. One pickup/dispatch record per accident. Pickup time is persisted here.
CREATE TABLE IF NOT EXISTS ambulance_dispatch (
    dispatch_id INT NOT NULL AUTO_INCREMENT,
    accident_id INT NOT NULL,
    driver_user_id INT NOT NULL,
    hospital_id INT NOT NULL,
    pickup_time DATETIME NOT NULL,
    dispatch_status VARCHAR(40) NOT NULL DEFAULT 'VICTIM_PICKED_UP',
    PRIMARY KEY (dispatch_id),
    UNIQUE KEY uq_dispatch_accident (accident_id),
    KEY idx_dispatch_hospital (hospital_id),
    KEY idx_dispatch_driver (driver_user_id),
    CONSTRAINT fk_dispatch_accident
        FOREIGN KEY (accident_id) REFERENCES accident (accident_id),
    CONSTRAINT fk_dispatch_driver
        FOREIGN KEY (driver_user_id) REFERENCES users (user_id),
    CONSTRAINT fk_dispatch_hospital
        FOREIGN KEY (hospital_id) REFERENCES hospitals (hospital_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Alerts created by ambulance pickup and displayed to hospital staff.
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT NOT NULL AUTO_INCREMENT,
    accident_id INT NOT NULL,
    hospital_id INT NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(40) NOT NULL DEFAULT 'HOSPITAL_ALERT',
    notification_status VARCHAR(20) NOT NULL DEFAULT 'SENT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (notification_id),
    KEY idx_notifications_hospital_created (hospital_id, created_at),
    KEY idx_notifications_accident (accident_id),
    CONSTRAINT fk_notification_accident
        FOREIGN KEY (accident_id) REFERENCES accident (accident_id),
    CONSTRAINT fk_notification_hospital
        FOREIGN KEY (hospital_id) REFERENCES hospitals (hospital_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Police log of relatives contacted about an accident.
CREATE TABLE IF NOT EXISTS relative_contact_log (
    contact_log_id INT NOT NULL AUTO_INCREMENT,
    accident_id INT NOT NULL,
    police_user_id INT NOT NULL,
    relative_name VARCHAR(100) NOT NULL,
    relative_phone VARCHAR(20) NOT NULL,
    contact_method VARCHAR(30) NOT NULL,
    informed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (contact_log_id),
    KEY idx_relative_log_accident_time (accident_id, informed_at),
    CONSTRAINT fk_relative_log_accident
        FOREIGN KEY (accident_id) REFERENCES accident (accident_id),
    CONSTRAINT fk_relative_log_police
        FOREIGN KEY (police_user_id) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Optional: create the first administrator account.
-- Change the phone number and password before running these lines.
-- Passwords are currently stored as plain text by this classroom prototype.
INSERT INTO users (
    full_name,
    phone_number,
    password_hash,
    role,
    zone,
    verification_status
)
VALUES (
    'System Admin',
    '0000000000',
    'ChangeThisPassword',
    'ADMIN',
    'KOZHIKODE',
    'APPROVED'
)
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    password_hash = VALUES(password_hash),
    role = VALUES(role),
    zone = VALUES(zone),
    verification_status = VALUES(verification_status);

-- 8. Verify the tables.
SHOW TABLES;
