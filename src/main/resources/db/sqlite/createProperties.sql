--liquibase formatted sql
--changeset milos.garunovic:createProperties
CREATE TABLE properties
(
    id                   TEXT PRIMARY KEY NOT NULL,
--     date_created         TEXT             NOT NULL,
--     active               INTEGER          NOT NULL DEFAULT 1,
--     date_deactivated     TEXT,
--     updated_by           TEXT,
--     date_updated         TEXT,
    registration_enabled INTEGER          NOT NULL DEFAULT 1,
    public_url_creation  INTEGER          NOT NULL DEFAULT 1
--     FOREIGN KEY (updated_by) REFERENCES users (id)
);

-- random id
INSERT INTO properties(id)
VALUES ('6e9c672e-867d-4257-b6d2-589554534bcf')