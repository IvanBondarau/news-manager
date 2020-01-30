CREATE table public.users
(
    id bigint NOT NULL AUTO_INCREMENT,
    name character varying(20) NOT NULL,
    surname character varying(20) NOT NULL,
    login character varying(30) NOT NULL,
    password character varying(30) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE public.roles
(
    user_id bigint NOT NULL,
    role_name character varying(30) NOT NULL,
    foreign key (user_id) references public.users(id)
);

CREATE TABLE public.author
(
    id bigint NOT NULL AUTO_INCREMENT,
    name character varying(30) NOT NULL,
    surname character varying(30) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE public.news
(
    id bigint NOT NULL AUTO_INCREMENT,
    title character varying(30) NOT NULL,
    short_text character varying(100) NOT NULL,
    full_text character varying(2000) NOT NULL,
    creation_date date NOT NULL,
    modification_date date NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE public.tag
(
    id bigint NOT NULL AUTO_INCREMENT,
    name character varying(30) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE public.news_author
(
    news_id bigint NOT NULL,
    author_id bigint NOT NULL,
    foreign key (news_id) references public.news(id),
    foreign key (author_id) references public.author(id)
);

CREATE TABLE public.news_tag
(
    news_id bigint NOT NULL,
    tag_id bigint NOT NULL,

    foreign key (news_id) references public.news(id),
    foreign key (tag_id) references public.tag(id)
);