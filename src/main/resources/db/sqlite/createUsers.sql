--liquibase formatted sql
--changeset milos.garunovic:createUsersTable
CREATE TABLE IF NOT EXISTS users
(
    id               TEXT PRIMARY KEY NOT NULL,
    email            TEXT UNIQUE      NOT NULL,
    password         TEXT             NOT NULL,
    date_created     INTEGER          NOT NULL,
    active           INTEGER          NOT NULL DEFAULT 1,
    date_deactivated INTEGER          NOT NULL DEFAULT 0
);