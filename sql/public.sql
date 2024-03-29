CREATE TABLE "resource"(
    id SERIAL PRIMARY KEY,
    type TEXT NOT NULL,
    content_type TEXT NOT NULL,
    url TEXT NOT NULL,
    width INTEGER NOT NULL,
    height INTEGER NOT NULL,
    created_utc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE "user"(
    id SERIAL PRIMARY KEY,
    thread_capacity INTEGER NOT NULL,
    created_utc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_utc TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE user_auth_token(
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES "user"(id) NOT NULL,
    token TEXT UNIQUE NOT NULL,
    expires_utc TIMESTAMP WITHOUT TIME ZONE,
    created_utc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE phone_number(
    id SERIAL PRIMARY KEY,
    number TEXT UNIQUE NOT NULL
);

CREATE TABLE device(
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES "user"(id) NOT NULL,
    ip INET NOT NULL,
    imei TEXT NOT NULL,
    phone_number_id INTEGER REFERENCES phone_number(id),
    last_seen_utc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_utc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_utc TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE device_phone_contact(
    id SERIAL PRIMARY KEY,
    device_id INTEGER REFERENCES device(id) NOT NULL,
    phone_number_id_of_contact INTEGER REFERENCES phone_number(id) NOT NULL,
    created_utc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_utc TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE abuse_flag(
    id SERIAL PRIMARY KEY,
    type TEXT NOT NULL,
    object_id INTEGER NOT NULL,
    user_id INTEGER REFERENCES "user"(id) NOT NULL,
    user_id_of_flagger INTEGER REFERENCES "user"(id) NOT NULL,
    created_utc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_utc TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE facebook_user(
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES "user"(id),
    id_facebook TEXT NOT NULL,
    access_token TEXT NOT NULL,
    name TEXT NOT NULL,
    gender TEXT,
    picture_url TEXT,
    created_utc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_utc TIMESTAMP WITHOUT TIME ZONE
);
CREATE UNIQUE INDEX ON facebook_user(id_facebook) WHERE is_deleted IS FALSE;

CREATE TABLE facebook_user_friend(
    id SERIAL PRIMARY KEY,
    facebook_user_id INTEGER REFERENCES facebook_user(id) NOT NULL,
    facebook_user_id_of_friend INTEGER REFERENCES facebook_user(id) NOT NULL,
    created_utc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_utc TIMESTAMP WITHOUT TIME ZONE
);

CREATE VIEW user_friend AS
SELECT
    fu.user_id AS user_id,
    ffu.user_id AS user_id_of_friend,
    fuf.created_utc AS created_utc,
    fuf.is_deleted OR ffu.is_deleted AS is_deleted,
    MAX(fuf.deleted_utc, ffu.deleted_utc) AS deleted_utc
FROM facebook_user_friend fuf
INNER JOIN facebook_user fu ON fu.id = fuf.facebook_user_id
INNER JOIN facebook_user ffu ON ffu.id = fuf.facebook_user_id_of_friend
WHERE NOT fu.is_deleted
UNION ALL
SELECT
    d.user_id AS user_id,
    fd.user_id AS user_id_of_friend,
    c.created_utc AS created_utc,
    c.is_deleted OR fd.is_deleted AS is_deleted,
    MAX(c.deleted_utc, fd.deleted_utc) AS deleted_utc
FROM device_phone_contact c
INNER JOIN device d ON d.id = c.device_id
INNER JOIN phone_number p ON p.id = c.phone_number_id_of_contact
INNER JOIN device fd ON fd.phone_number_id = p.id
WHERE NOT d.is_deleted;

CREATE TABLE thread(
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    image_url TEXT,
    device_id INTEGER REFERENCES device(id),
    last_updated_utc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_utc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_utc TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE thread_user(
    id SERIAL PRIMARY KEY,
    thread_id INTEGER REFERENCES thread(id) NOT NULL,
    user_id INTEGER REFERENCES user(id) NOT NULL,
    icon_url TEXT NOT NULL,
    colour_red INTEGER NOT NULL,
    colour_green INTEGER NOT NULL,
    colour_blue INTEGER NOT NULL,
    is_owner BOOLEAN NOT NULL,
    created_utc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_utc TIMESTAMP WITHOUT TIME ZONE
);
CREATE UNIQUE INDEX ON thread_user(thread_id, colour_red, colour_green, colour_blue, icon_url)
WHERE NOT is_deleted;
CREATE UNIQUE INDEX ON thread_user(thread_id, user_id) WHERE NOT is_deleted;

CREATE TABLE thread_message(
    id SERIAL PRIMARY KEY,
    thread_user_id INTEGER REFERENCES thread_user(id) NOT NULL,
    "text" TEXT NOT NULL,
    image_url TEXT,
    device_id INTEGER REFERENCES device(id),
    created_utc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_utc TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE thread_user_message_read(
    id SERIAL PRIMARY KEY,
    thread_user_id INTEGER REFERENCES thread_user(id) NOT NULL,
    thread_message_id INTEGER REFERENCES thread_message(id) NOT NULL,
    created_utc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    UNIQUE(thread_user_id, thread_message_id)
);
