CREATE TABLE `portfolio`.`image_integrate` (
                                             `idx` INT NOT NULL AUTO_INCREMENT,
                                             `ref_id` INT NOT NULL,
                                             `ref_table` VARCHAR(255) NOT NULL,
                                             `url` VARCHAR(255) NOT NULL,
                                             `s3_key` VARCHAR(255) NOT NULL,
                                             `bucket` VARCHAR(255) NOT NULL,
                                             `status` VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                                             PRIMARY KEY (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
