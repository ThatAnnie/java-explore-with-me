DROP TABLE IF EXISTS users, categories, events, compilations, requests, compilation_event, comments;

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    CONSTRAINT uc_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories (
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    CONSTRAINT uc_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS compilations (
    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title  VARCHAR(50) NOT NULL,
    pinned BOOLEAN,
    CONSTRAINT uc_compilation_title UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS events (
    event_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT,
    description VARCHAR(7000) NOT NULL,
    title VARCHAR(120) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE,
    lat FLOAT,
    lon FLOAT,
    initiator_id BIGINT,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    paid BOOLEAN,
    participant_limit INT DEFAULT 0,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN DEFAULT TRUE,
    state VARCHAR(20) NOT NULL,
    CONSTRAINT fk_event_category FOREIGN KEY (category_id) REFERENCES categories (category_id),
    CONSTRAINT fk_event_user FOREIGN KEY (initiator_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS requests (
    request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created TIMESTAMP WITHOUT TIME ZONE,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_event_request FOREIGN KEY (event_id) REFERENCES events (event_id),
    CONSTRAINT fk_user_request FOREIGN KEY (requester_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS compilation_event (
    compilation_id BIGINT REFERENCES compilations (compilation_id),
    event_id BIGINT REFERENCES events (event_id),
    PRIMARY KEY (compilation_id, event_id),
    CONSTRAINT fk_compilation_event_compilation_id FOREIGN KEY(compilation_id) REFERENCES compilations (compilation_id) ON DELETE CASCADE,
    CONSTRAINT fk_compilation_event_event_id FOREIGN KEY(event_id) REFERENCES events (event_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR(2000) NOT NULL,
    event_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    edited_on TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_comment_event FOREIGN KEY (event_id) REFERENCES events (event_id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (author_id) REFERENCES users (user_id) ON DELETE CASCADE
);
