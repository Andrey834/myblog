CREATE TABLE if not exists post
(
    id          bigserial PRIMARY KEY,
    title       varchar(250)  NOT NULL,
    text        varchar(5000) NOT NULL,
    image varchar(500)  NOT NULL UNIQUE,
    created     timestamp default current_timestamp,
    likes       int8      default 0
);

CREATE TABLE if not exists tag
(
    id    bigserial PRIMARY KEY,
    title varchar(50) NOT NULL UNIQUE
);

CREATE TABLE if not exists post_tag
(
    post_id int8 references post (id) ON DELETE CASCADE NOT NULL,
    tag_id  int8 references tag (id) ON DELETE CASCADE  NOT NULL,

    constraint unq_post_tag_id UNIQUE (post_id, tag_id)
);

CREATE TABLE if not exists comment
(
    id      bigserial PRIMARY KEY,
    post_id int8 references post (id) ON DELETE CASCADE NOT NULL,
    text    varchar(500)                                NOT NULL,
    created timestamp default current_timestamp
);
