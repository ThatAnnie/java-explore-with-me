DROP TABLE IF EXISTS stats;

CREATE TABLE IF NOT EXISTS stats
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app VARCHAR(64) NOT NULL,
    uri VARCHAR(250) NOT NULL,
    ip VARCHAR(64) NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);