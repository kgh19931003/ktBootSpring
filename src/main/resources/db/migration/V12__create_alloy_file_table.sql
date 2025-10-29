CREATE TABLE `alloy_file` (
                                  `idx` INT NOT NULL AUTO_INCREMENT,
                                  `language` VARCHAR(10),
                                  `parent_idx` INT NOT NULL,
                                  `origin_name` VARCHAR(255),
                                  `name` VARCHAR(255),
                                  `dir` VARCHAR(255),
                                  `src` VARCHAR(255),
                                  `size` BIGINT,
                                  `content_type` VARCHAR(255),
                                  `order` INT DEFAULT 0,
                                  `uuid` VARCHAR(255),
                                  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
                                  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  `deleted_at` timestamp DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
