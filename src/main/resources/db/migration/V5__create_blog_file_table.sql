CREATE TABLE `blog_file` (
                             `idx` INT NOT NULL AUTO_INCREMENT,
                             `language` VARCHAR(10),
                             `parent_idx` int(11) NOT NULL,
                             `origin_name` varchar(255) DEFAULT NULL,
                             `name` varchar(255) DEFAULT NULL,
                             `dir` varchar(255) DEFAULT NULL,
                             `src` varchar(255) DEFAULT NULL,
                             `size` double DEFAULT NULL,
                             `content_type` varchar(255) DEFAULT NULL,
                             `order` int(11) DEFAULT 0,
                             `uuid` varchar(255) DEFAULT NULL,
                             `created_at` timestamp DEFAULT current_timestamp(),
                             `updated_at` timestamp DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                             `deleted_at` timestamp DEFAULT NULL,
                             PRIMARY KEY (`idx`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci