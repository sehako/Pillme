DROP DATABASE IF EXISTS `pillme`;

CREATE DATABASE IF NOT EXISTS `pillme`;

USE `pillme`;

DROP TABLE IF EXISTS `hospital`;

CREATE TABLE `hospital`
(
    `id`      BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name`    VARCHAR(100) NOT NULL,
    `region`  VARCHAR(30)  NOT NULL,
    `post`    INT          NOT NULL,
    `address` VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS `management`;

CREATE TABLE `management`
(
    `id`              BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `information_id`  BIGINT    NOT NULL,
    `medication_name` BIGINT    NOT NULL,
    `serving_size`    INT       NULL,
    `period`          INT       NULL,
    `morning`         TINYINT   NULL,
    `lunch`           TINYINT   NULL,
    `dinner`          TINYINT   NULL,
    `sleep`           TINYINT   NULL,
    `morning_taking`  TINYINT   NULL,
    `lunch_taking`    TINYINT   NULL,
    `dinner_taking`   TINYINT   NULL,
    `sleep_taking`    TINYINT   NULL,
    `created_at`      TIMESTAMP NULL,
    `modified_at`     TIMESTAMP NULL,
    `deleted`         TIMESTAMP NULL
);

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `community_id` BIGINT       NOT NULL,
    `member_id`    BIGINT       NOT NULL,
    `content`      VARCHAR(500) NULL
);

DROP TABLE IF EXISTS `fcm_token`;

CREATE TABLE `fcm_token`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `member_id`   BIGINT       NOT NULL,
    `token`       VARCHAR(255) NULL,
    `created_at`  TIMESTAMP    NULL,
    `modified_at` TIMESTAMP    NULL
);

DROP TABLE IF EXISTS `information`;

CREATE TABLE `information`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `reader`       BIGINT       NOT NULL,
    `writer`       BIGINT       NOT NULL,
    `hospital`     VARCHAR(100) NULL,
    `start_date`   DATE         NULL,
    `end_date`     DATE         NULL,
    `disease_name` VARCHAR(255) NULL,
    `created_at`   TIMESTAMP    NULL,
    `modified_at`  TIMESTAMP    NULL,
    `supplement`   TINYINT      NULL,
    `deleted`      TINYINT      NULL
);

DROP TABLE IF EXISTS `fcm_log`;

CREATE TABLE `fcm_log`
(
    `id`        BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `member_id` BIGINT       NOT NULL,
    `Field`     VARCHAR(255) NULL,
    `Field2`    VARCHAR(255) NULL,
    `status`    VARCHAR(50)  NULL,
    `sent_at`   TIMESTAMP    NULL,
    `Field3`    VARCHAR(255) NULL
);

DROP TABLE IF EXISTS `community`;

CREATE TABLE `community`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `member_id`   BIGINT       NOT NULL,
    `title`       VARCHAR(150) NULL,
    `content`     TEXT         NULL,
    `created_at`  TIMESTAMP    NULL,
    `modified_at` TIMESTAMP    NULL,
    `deleted`     TINYINT      NULL
);

DROP TABLE IF EXISTS `notification_setting`;

CREATE TABLE `notification_setting`
(
    `id`        BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `member_id` BIGINT NOT NULL,
    `morning`   TIME   NULL,
    `lunch`     TIME   NULL,
    `dinner`    TIME   NULL,
    `sleep`     TIME   NULL
);

DROP TABLE IF EXISTS `notification`;

CREATE TABLE `notification`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `sender`      BIGINT       NOT NULL,
    `receiver`    BIGINT       NOT NULL,
    `code`        VARCHAR(20)  NULL,
    `content`     VARCHAR(255) NULL,
    `created_at`  TIMESTAMP    NULL,
    `modified_at` TIMESTAMP    NULL,
    `confirm`     TINYINT      NULL,
    `deleted`     TINYINT      NULL
);

DROP TABLE IF EXISTS `chat_room`;

CREATE TABLE `chat_room`
(
    `id`             BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `post_member_id` BIGINT NOT NULL,
    `member_id`      BIGINT NOT NULL
);

DROP TABLE IF EXISTS `chat_message`;

CREATE TABLE `chat_message`
(
    `id`         BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `member_id`  BIGINT        NOT NULL,
    `chat_id`    BIGINT        NOT NULL,
    `message`    VARCHAR(3000) NULL,
    `created_at` TIMESTAMP     NULL
);

DROP TABLE IF EXISTS `sub_comment`;

CREATE TABLE `sub_comment`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `comment_id` BIGINT       NOT NULL,
    `member_id`  BIGINT       NOT NULL,
    `content`    VARCHAR(500) NULL
);

DROP TABLE IF EXISTS `dependency`;

CREATE TABLE `dependency`
(
    `id`            BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `administrator` BIGINT    NOT NULL,
    `member`        BIGINT    NOT NULL,
    `created_at`    TIMESTAMP NULL,
    `modified_at`   TIMESTAMP NULL,
    `deleted`       TINYINT   NULL
);

DROP TABLE IF EXISTS `image`;

CREATE TABLE `image`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `community_id` BIGINT       NOT NULL,
    `path`         VARCHAR(100) NULL
);

DROP TABLE IF EXISTS `member`;

CREATE TABLE `member`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `email`       VARCHAR(50)  NULL,
    `password`    VARCHAR(300) NULL,
    `name`        VARCHAR(30)  NULL,
    `nickname`    VARCHAR(30)  NULL,
    `role`        VARCHAR(50)  NULL,
    `gender`      VARCHAR(1)   NULL,
    `phone`       VARCHAR(30)  NULL,
    `created_at`  TIMESTAMP    NULL,
    `modified_at` TIMESTAMP    NULL,
    `deleted`     TINYINT      NULL,
    `oauth`       TINYINT      NULL,
    `birthday`    VARCHAR(10)  NULL
);

DROP TABLE IF EXISTS `history`;

CREATE TABLE `history`
(
    `id`             BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `management_id`  BIGINT    NOT NULL,
    `member_id`      BIGINT    NOT NULL,
    `information_id` BIGINT    NOT NULL,
    `morning`        TINYINT   NULL,
    `lunch`          TINYINT   NULL,
    `dinner`         TINYINT   NULL,
    `sleep`          TINYINT   NULL,
    `morning_taking` TINYINT   NULL,
    `lunch_taking`   TINYINT   NULL,
    `dinner_taking`  TINYINT   NULL,
    `sleep_taking`   TINYINT   NULL,
    `taking_date`    DATE      NULL,
    `created_at`     TIMESTAMP NULL,
    `modified_at`    TIMESTAMP NULL,
    `deleted`        TINYINT   NULL
);

