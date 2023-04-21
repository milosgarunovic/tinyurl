--liquibase formatted sql
--changeset milos.garunovic:createUrlStatisticsTable
CREATE TABLE url_statistics
(
    id               TEXT PRIMARY KEY NOT NULL,
    date_created     TEXT             NOT NULL,
    active           INTEGER          NOT NULL DEFAULT 1,
    date_deactivated TEXT,
    url              TEXT             NOT NULL,
    short_url        TEXT             NOT NULL,
    user_id          TEXT             NOT NULL,
    FOREIGN KEY (short_url) REFERENCES urls (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
)