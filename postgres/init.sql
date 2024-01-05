\c notes_db;

create table if not exists users
(
    id                   int generated always as identity not null primary key,
    email                text                             not null,
    username             text                             null,
    password             text                             null,
    totp_secret          text                             null,
    is_verified          boolean                                   default false,
    is_user_non_locked   boolean                                   default true,
    failed_login_attempt int                              not null default 0,
    lock_time            timestamp                        not null default make_timestamp(1970, 0, 0, 0, 0, 0)
);

create table if not exists note
(
    id               int generated always as identity not null primary key,
    title            text                             not null,
    content          text                             null,
    owner_id         int                              not null,

    encoded_password text                             null,
    salt             bytea                            null,
    iv               bytea                            null,

    is_encrypted     boolean default false,
    is_published     boolean default false
);