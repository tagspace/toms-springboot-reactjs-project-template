
create table users (
    id uuid primary key,
    email varchar(512) not null,
    created_at timestamptz not null default now()
);
