CREATE TABLE `member` (
                             `idx` INT NOT NULL AUTO_INCREMENT,
                             `language` VARCHAR(10),
                             `id` VARCHAR(50) NOT NULL UNIQUE,
                             `password` VARCHAR(255),
                             `name` VARCHAR(50),
                             `gender` VARCHAR(1),
                             `profile` VARCHAR(255),
                             `biz_reg_number` VARCHAR(30),
                             `biz_reg_cert` VARCHAR(255),
                             `level` INT,
                             `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
                             `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             `deleted_at` timestamp NULL,
                             `access_token` VARCHAR(255),
                             `refresh_token` VARCHAR(255),
                             PRIMARY KEY (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;