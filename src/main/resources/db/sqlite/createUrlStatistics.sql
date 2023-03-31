--liquibase formatted sql
--changeset milos.garunovic:createUrlStatisticsTable
CREATE TABLE IF NOT EXISTS url_statistics
(
    id               TEXT PRIMARY KEY NOT NULL,
    date_created     INTEGER          NOT NULL,
    active           INTEGER          NOT NULL DEFAULT 1,
    date_deactivated INTEGER          NOT NULL DEFAULT 0,
    url              TEXT             NOT NULL,
    short_url        TEXT             NOT NULL,
    user_id          TEXT             NOT NULL,
    FOREIGN KEY (short_url) REFERENCES urls (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
)