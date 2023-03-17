--liquibase formatted sql
--changeset milos.garunovic:createUrlsTable
CREATE TABLE IF NOT EXISTS url
(
    id                TEXT PRIMARY KEY NOT NULL,
    short_url         TEXT             NOT NULL UNIQUE,
    url               TEXT             NOT NULL,
    calculated_expiry INTEGER          NOT NULL DEFAULT 0,
    date_created      INTEGER          NOT NULL,
    active            INTEGER          NOT NULL DEFAULT 1,
    date_deactivated  INTEGER          NOT NULL DEFAULT 0,
    user_id           TEXT,
    FOREIGN KEY (user_id) REFERENCES users (id)
)