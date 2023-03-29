--liquibase formatted sql
--changeset milos.garunovic:createUrlStatisticsTable
CREATE TABLE IF NOT EXISTS url_statistics
(
    id               TEXT PRIMARY KEY NOT NULL,
    date_created     INTEGER          NOT NULL,
    active           INTEGER          NOT NULL DEFAULT 1,
    date_deactivated INTEGER          NOT NULL DEFAULT 0,
    url_id           TEXT             NOT NULL,
    user_id          TEXT             NOT NULL,
    FOREIGN KEY (url_id) REFERENCES url (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
)