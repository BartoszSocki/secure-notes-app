\c notes_db;

create table if not exists users
(
    id         	int generated always as identity not null primary key,
    email       text not null,
    username    text null,
    password    text null,
    totp_secret text null,
    is_verified boolean default false
);

