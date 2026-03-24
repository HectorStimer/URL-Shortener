create table clicks(
    id BIGSERIAL not null primary key,
    url BIGINT not null,
    clicked_at timestamp not null,
    ip_address varchar(15),
    user_agent text,
    referer text,

    constraint fk_url
        foreign key(url)
        references urls(id)
);