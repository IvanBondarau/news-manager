CREATE table public."user"
(
    id bigint NOT NULL,
    name character varying(20) NOT NULL,
    surname character varying(20) NOT NULL,
    login character varying(30) NOT NULL,
    password character varying(30) NOT NULL,
    PRIMARY KEY (id)
) with (
    OIDS = false
);

ALTER TABLE public."user"
    OWNER to postgres;

GRANT ALL ON TABLE public."user" TO "newsManager";

CREATE TABLE public.roles
(
    user_id bigint NOT NULL,
    role_name character varying(30) NOT NULL,
    CONSTRAINT user_id_fk FOREIGN KEY (user_id)
        REFERENCES public."user" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
WITH (
    OIDS = FALSE
);

ALTER TABLE public.roles
    OWNER to postgres;

GRANT ALL ON TABLE public."roles" TO "newsManager";

CREATE TABLE public.author
(
    id bigint NOT NULL,
    name character varying(30) NOT NULL,
    surname character varying(30) NOT NULL,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
);

ALTER TABLE public.author
    OWNER to "newsManager";

GRANT ALL ON TABLE public.author TO "newsManager";

CREATE TABLE public.news
(
    id bigint NOT NULL,
    title character varying(30) NOT NULL,
    short_text character varying(100) NOT NULL,
    full_text character varying(2000) NOT NULL,
    creation_date date NOT NULL,
    modification_date date NOT NULL,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
);

ALTER TABLE public.news
    OWNER to postgres;

GRANT ALL ON TABLE public.news TO "newsManager";

CREATE TABLE public.tag
(
    id bigint NOT NULL,
    name character varying(30) NOT NULL,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
);

ALTER TABLE public.tag
    OWNER to postgres;

GRANT ALL ON TABLE public.tag TO "newsManager";

CREATE TABLE public.news_author
(
    news_id bigint NOT NULL,
    author_id bigint NOT NULL,
    CONSTRAINT news_id_fk FOREIGN KEY (news_id)
        REFERENCES public.news (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT author_id_fk FOREIGN KEY (author_id)
        REFERENCES public.author (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
WITH (
    OIDS = FALSE
);

ALTER TABLE public.news_author
    OWNER to postgres;

GRANT ALL ON TABLE public.news_author TO "newsManager";

CREATE TABLE public.news_tag
(
    news_id bigint NOT NULL,
    tag_id bigint NOT NULL,
    CONSTRAINT news_id_fk FOREIGN KEY (news_id)
        REFERENCES public.news (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT tag_id_fk FOREIGN KEY (tag_id)
        REFERENCES public.tag (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
WITH (
    OIDS = FALSE
);

ALTER TABLE public.news_tag
    OWNER to postgres;

GRANT ALL ON TABLE public.news_tag TO "newsManager";