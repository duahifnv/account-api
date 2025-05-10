create table users (
    id bigserial primary key,
    name varchar(500) not null,
    date_of_birth date not null check (to_char(date_of_birth, 'DD.MM.YYYY') ~ '^\d{2}\.\d{2}\.\d{4}$'),
    password varchar(500) not null check (length(password) >= 8)
);

create table accounts (
    id bigserial primary key,
    user_id bigint not null unique references users(id),
    balance decimal(19, 4) not null default 0,
    max_balance decimal(19, 4) not null default 0,
    last_balance_update timestamp not null default current_timestamp
);

create table email_data (
    id bigserial primary key,
    user_id bigint not null references users(id),
    email varchar(200) not null unique
);

create table phone_data (
    id bigserial primary key,
    user_id bigint not null references users(id),
    phone varchar(13) not null unique check (phone ~ '^7[0-9]{10}$')
);
