CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `city` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE (`username`),
    UNIQUE (`email`),
    UNIQUE (`phone`)
);

CREATE TABLE `item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `description` VARCHAR(200) NOT NULL,
    `conditions` VARCHAR(60),
    `category` ENUM('EDUCATION','TECHNOLOGY','TRANSPORT','FURNITURE','CLOTHES','SPORT','OTHER') NOT NULL,
    `price` DECIMAL(10,2) NOT NULL,
    `image` LONGBLOB NOT NULL,
    `owner_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_item_owner` FOREIGN KEY (`owner_id`) REFERENCES `user`(`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE `rent` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `start_date` DATETIME NOT NULL,
    `end_date` DATETIME NOT NULL,
    `item_id` BIGINT NOT NULL,
    `rentier_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_rent_item` FOREIGN KEY (`item_id`) REFERENCES `item`(`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_rent_rentier` FOREIGN KEY (`rentier_id`) REFERENCES `user`(`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
