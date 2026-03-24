create table urls (
    id BIGSERIAL primary key ,
    original_url text not null,
    short_code varchar(50) not null UNIQUE ,
    created_at timestamp not null ,
    expires_at timestamp not null

);