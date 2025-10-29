CREATE TABLE `policy` (
                             `idx` INT NOT NULL AUTO_INCREMENT,
                             `language` VARCHAR(10),
                             `type` varchar(50) NOT NULL,
                             `content` longtext DEFAULT NULL,
                             `created_at` timestamp DEFAULT current_timestamp(),
                             `updated_at` timestamp DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                             `deleted_at` timestamp DEFAULT NULL,
                             PRIMARY KEY (`idx`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci