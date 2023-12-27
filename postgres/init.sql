\c notes_db;

create table if not exists users
(
    id          int generated always as identity not null primary key,
    email       text                             not null,
    username    text                             null,
    password    text                             null,
    totp_secret text                             null,
    is_verified boolean default false
);

create table if not exists note
(
    id               int generated always as identity not null primary key,
    title            text                             not null,
    content          text                             null,

    encoded_password text                             null,
    salt             bytea                            null,
    iv               bytea                            null,

    is_encrypted     boolean default false,
    is_published     boolean default false
);