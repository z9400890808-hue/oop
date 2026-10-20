USE road_accident_db;

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
