create table users (
    user_id bigserial constraint users_pk primary key,
    email varchar(50) not null constraint users_email_unique unique,
    password text not null,
    pin varchar(50) not null,
    profile_picture_url varchar(100),
    is_onboarding_finished boolean default false not null,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    deleted_at timestamp with time zone
);

create unique index users_email_uindex on users (email);

create unique index users_user_id_uindex on users (user_id desc);