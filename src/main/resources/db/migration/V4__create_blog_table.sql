CREATE TABLE `blog` (
                        `idx` INT NOT NULL AUTO_INCREMENT,
                        `language` VARCHAR(10),
                        `source_organ` varchar(50) DEFAULT NULL,
                        `title` varchar(255) DEFAULT NULL,
                        `subtitle` varchar(255) DEFAULT NULL,
                        `content` longtext DEFAULT NULL,
                        `category` varchar(30) DEFAULT NULL,
                        `reg_date` varchar(50) DEFAULT NULL,
                        `secret` varchar(1) NOT NULL DEFAULT 'N',
                        `editor_image` longtext DEFAULT NULL,
                        `created_at` timestamp DEFAULT current_timestamp(),
                        `updated_at` timestamp DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                        `deleted_at` timestamp DEFAULT NULL,
                        PRIMARY KEY (`idx`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci