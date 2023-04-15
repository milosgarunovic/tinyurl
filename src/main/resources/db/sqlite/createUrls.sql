--liquibase formatted sql
--changeset milos.garunovic:createUrlsTable
CREATE TABLE IF NOT EXISTS urls
(
    id               TEXT PRIMARY KEY NOT NULL,
    short_url        TEXT             NOT NULL UNIQUE,
    url              TEXT             NOT NULL,
    expiry           TEXT,
    date_created     TEXT             NOT NULL,
    active           INTEGER          NOT NULL DEFAULT 1,
    date_deactivated TEXT,
    user_id          TEXT,
    FOREIGN KEY (user_id) REFERENCES users (id)
)