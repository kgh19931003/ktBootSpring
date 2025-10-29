CREATE TABLE `inquiry` (
                             `idx` INT NOT NULL AUTO_INCREMENT,
                             `language` VARCHAR(10),
                             `category` varchar(50) NOT NULL,
                             `company_name` varchar(255) DEFAULT NULL,
                             `manager` varchar(255) DEFAULT NULL,
                             `tel` varchar(255) DEFAULT NULL,
                             `email` varchar(255) DEFAULT NULL,
                             `content` longtext DEFAULT NULL,
                             `image_url` text DEFAULT NULL,
                             `private_agree` varchar(1) DEFAULT 'N',
                             `created_at` timestamp DEFAULT current_timestamp(),
                             `updated_at` timestamp DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                             `deleted_at` timestamp DEFAULT NULL,
                             PRIMARY KEY (`idx`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci