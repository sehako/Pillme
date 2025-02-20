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

DROP TABLE IF EXISTS `medication`;

CREATE TABLE `medication`
(
    `id`      BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name`    VARCHAR(255) NULL,
    `company` VARCHAR(100) NULL,
    `image`   VARCHAR(255) NULL
);

DROP TABLE IF EXISTS `management`;

CREATE TABLE `management`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `information_id`  BIGINT       NULL,
    `medication_name` VARCHAR(255) NULL,
    `period`          INT          NULL,
    `morning`         TINYINT      NULL,
    `lunch`           TINYINT      NULL,
    `dinner`          TINYINT      NULL,
    `sleep`           TINYINT      NULL,
    `morning_taking`  TINYINT      NULL,
    `lunch_taking`    TINYINT      NULL,
    `dinner_taking`   TINYINT      NULL,
    `sleep_taking`    TINYINT      NULL,
    `created_at`      TIMESTAMP    NULL DEFAULT NOW(),
    `modified_at`     TIMESTAMP    NULL DEFAULT NOW(),
    `deleted`         TINYINT      NULL DEFAULT false
);

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `community_id` BIGINT       NULL,
    `member_id`    BIGINT       NULL,
    `content`      VARCHAR(500) NULL
);

DROP TABLE IF EXISTS `fcm_token`;

CREATE TABLE `fcm_token`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `member_id`   BIGINT       NOT NULL,
    `token`       VARCHAR(255) NULL,
    `created_at`  TIMESTAMP    NULL DEFAULT NOW(),
    `modified_at` TIMESTAMP    NULL DEFAULT NOW(),
    `deleted`     TINYINT      NULL DEFAULT false
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
    `requested`    TINYINT      NULL,
    `created_at`   TIMESTAMP    NULL DEFAULT NOW(),
    `modified_at`  TIMESTAMP    NULL DEFAULT NOW(),
    `deleted`      TINYINT      NULL DEFAULT false
);

DROP TABLE IF EXISTS `fcm_log`;

CREATE TABLE `fcm_log`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `member_id`       BIGINT       NOT NULL,
    `title`           VARCHAR(20)  NULL,
    `content`         VARCHAR(255) NULL,
    `status`          VARCHAR(50)  NULL,
    `retry_count`     BIGINT       NULL,
    `initial_send_at` TIMESTAMP    NULL,
    `send_at`         TIMESTAMP    NULL
);

DROP TABLE IF EXISTS `community`;

CREATE TABLE `community`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `member_id`   BIGINT       NOT NULL,
    `title`       VARCHAR(150) NULL,
    `content`     TEXT         NULL,
    `created_at`  TIMESTAMP    NULL DEFAULT NOW(),
    `modified_at` TIMESTAMP    NULL DEFAULT NOW(),
    `deleted`     TINYINT      NULL DEFAULT false
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
    `sender_id`   BIGINT       NOT NULL,
    `receiver_id` BIGINT       NOT NULL,
    `code`        VARCHAR(60)  NULL,
    `content`     VARCHAR(255) NULL,
    `confirm`     TINYINT      NULL,
    `created_at`  TIMESTAMP    NULL DEFAULT NOW(),
    `modified_at` TIMESTAMP    NULL DEFAULT NOW(),
    `deleted`     TINYINT      NULL DEFAULT false
);

DROP TABLE IF EXISTS `chat_room`;

CREATE TABLE `chat_room`
(
    `id`              BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `send_user_id`    BIGINT NOT NULL,
    `receive_user_id` BIGINT NOT NULL
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
    `id`           BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `protector_id` BIGINT    NOT NULL,
    `dependent_id` BIGINT    NOT NULL,
    `created_at`   TIMESTAMP NULL DEFAULT NOW(),
    `modified_at`  TIMESTAMP NULL DEFAULT NOW(),
    `deleted`      TINYINT   NULL DEFAULT false
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
    `created_at`  TIMESTAMP    NULL DEFAULT NOW(),
    `modified_at` TIMESTAMP    NULL DEFAULT NOW(),
    `deleted`     TINYINT      NULL DEFAULT false,
    `oauth`       TINYINT      NULL,
    `birthday`    VARCHAR(10)  NULL,
    `provider`    VARCHAR(50)  NULL
);

DROP TABLE IF EXISTS `history`;

CREATE TABLE `history`
(
    `id`             BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `management_id`  BIGINT    NOT NULL,
    `member_id`      BIGINT    NOT NULL,
    `information_id` BIGINT    NOT NULL,
    `period`         INTEGER   NULL,
    `morning`        TINYINT   NULL,
    `lunch`          TINYINT   NULL,
    `dinner`         TINYINT   NULL,
    `sleep`          TINYINT   NULL,
    `morning_taking` TINYINT   NULL,
    `lunch_taking`   TINYINT   NULL,
    `dinner_taking`  TINYINT   NULL,
    `sleep_taking`   TINYINT   NULL,
    `taking_date`    DATE      NULL,
    `created_at`     TIMESTAMP NULL DEFAULT NOW(),
    `modified_at`    TIMESTAMP NULL DEFAULT NOW(),
    `deleted`        TINYINT   NULL DEFAULT false
);

SELECT *
FROM member;

INSERT INTO member (email, password, name, nickname, role, gender, phone, deleted, oauth, birthday, provider)
VALUES ('parent@naver.com', '$2a$10$JIZkqHg8UeFe1KzE4/URyu3g3NrF3jM44w0vGQ8SnXmoA0D4qT2Bm', '피보호자', '피보호자', 'USER', 'F',
        '01011111111', false, false, '19600324', 'FORM');
INSERT INTO member (email, password, name, nickname, role, gender, phone, deleted, oauth, birthday, provider)
VALUES ('child@naver.com', '$2a$10$JIZkqHg8UeFe1KzE4/URyu3g3NrF3jM44w0vGQ8SnXmoA0D4qT2Bm', '보호자', '보호자', 'USER', 'F',
        '01022222222', false, false, '19871211', 'FORM');
INSERT INTO member (email, password, name, nickname, role, gender, phone, deleted, oauth, birthday, provider)
VALUES ('test@naver.com', '$2a$10$JIZkqHg8UeFe1KzE4/URyu3g3NrF3jM44w0vGQ8SnXmoA0D4qT2Bm', '테스트', '테스트', 'USER', 'F',
        '01033333333', false, false, '19871211', 'FORM');