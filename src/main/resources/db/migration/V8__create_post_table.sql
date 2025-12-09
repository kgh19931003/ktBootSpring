CREATE TABLE `post` (
                           `idx` INT NOT NULL AUTO_INCREMENT,
                           `language` VARCHAR(10),
                           `category` VARCHAR(50) NOT NULL,
                           `title` VARCHAR(255) NOT NULL,
                           `subtitle` longtext DEFAULT NULL,
                           `thumbnail` VARCHAR(255),
                           `content` longtext DEFAULT NULL,
                           `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
                           `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           `deleted_at` timestamp NULL,
                           PRIMARY KEY (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;