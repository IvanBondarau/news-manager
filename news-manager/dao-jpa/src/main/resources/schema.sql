--
-- PostgreSQL database dump
--

-- Dumped from database version 14.4
-- Dumped by pg_dump version 14.4

-- Started on 2022-07-26 19:15:26

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3 (class 2615 OID 2200)
-- Name: news_manager; Type: SCHEMA; Schema: -; Owner: postgres
--
DROP SCHEMA IF EXISTS news_manager CASCADE;
CREATE SCHEMA news_manager;


ALTER SCHEMA news_manager OWNER TO postgres;

--
-- TOC entry 3347 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA news_manager; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA news_manager IS 'Main schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 211 (class 1259 OID 16408)
-- Name: author; Type: TABLE; Schema: news_manager; Owner: postgres
--

CREATE TABLE news_manager.author (
                                     id bigint NOT NULL,
                                     name character varying(50) NOT NULL,
                                     surname character varying(50) NOT NULL
);


ALTER TABLE news_manager.author OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 16413)
-- Name: news; Type: TABLE; Schema: news_manager; Owner: postgres
--

CREATE TABLE news_manager.news (
                                   id bigint NOT NULL,
                                   title character varying(30) NOT NULL,
                                   short_text character varying(100) NOT NULL,
                                   full_text character varying(2000) NOT NULL,
                                   creation_date date NOT NULL,
                                   modification_date date NOT NULL
);


ALTER TABLE news_manager.news OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 16425)
-- Name: news_author; Type: TABLE; Schema: news_manager; Owner: postgres
--

CREATE TABLE news_manager.news_author (
                                          news_id bigint NOT NULL,
                                          author_id bigint NOT NULL
);


ALTER TABLE news_manager.news_author OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 16438)
-- Name: news_tag; Type: TABLE; Schema: news_manager; Owner: postgres
--

CREATE TABLE news_manager.news_tag (
                                       news_id bigint NOT NULL,
                                       tag_id bigint NOT NULL
);


ALTER TABLE news_manager.news_tag OWNER TO postgres;

--
-- TOC entry 213 (class 1259 OID 16420)
-- Name: tag; Type: TABLE; Schema: news_manager; Owner: postgres
--

CREATE TABLE news_manager.tag (
                                  id bigint NOT NULL,
                                  name character varying(30) NOT NULL
);


ALTER TABLE news_manager.tag OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 16395)
-- Name: user; Type: TABLE; Schema: news_manager; Owner: postgres
--

CREATE TABLE news_manager."user" (
                                     id bigint NOT NULL,
                                     name character varying(50) NOT NULL,
                                     surname character varying(50) NOT NULL,
                                     login character varying(50) NOT NULL,
                                     password character(128) NOT NULL
);


ALTER TABLE news_manager."user" OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 16400)
-- Name: user_role; Type: TABLE; Schema: news_manager; Owner: postgres
--

CREATE TABLE news_manager.user_role (
                                        user_id bigint NOT NULL,
                                        role_name character varying(30) NOT NULL
);


ALTER TABLE news_manager.user_role OWNER TO postgres;

--
-- TOC entry 3193 (class 2606 OID 16412)
-- Name: author author_pkey; Type: CONSTRAINT; Schema: news_manager; Owner: postgres
--

ALTER TABLE ONLY news_manager.author
    ADD CONSTRAINT author_pkey PRIMARY KEY (id);


--
-- TOC entry 3195 (class 2606 OID 16419)
-- Name: news news_pkey; Type: CONSTRAINT; Schema: news_manager; Owner: postgres
--

ALTER TABLE ONLY news_manager.news
    ADD CONSTRAINT news_pkey PRIMARY KEY (id);


--
-- TOC entry 3191 (class 2606 OID 16453)
-- Name: user_role pk_role; Type: CONSTRAINT; Schema: news_manager; Owner: postgres
--

ALTER TABLE ONLY news_manager.user_role
    ADD CONSTRAINT pk_role PRIMARY KEY (user_id, role_name);


--
-- TOC entry 3197 (class 2606 OID 16424)
-- Name: tag tag_pkey; Type: CONSTRAINT; Schema: news_manager; Owner: postgres
--

ALTER TABLE ONLY news_manager.tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (id);


--
-- TOC entry 3188 (class 2606 OID 16399)
-- Name: user users_pkey; Type: CONSTRAINT; Schema: news_manager; Owner: postgres
--

ALTER TABLE ONLY news_manager."user"
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3189 (class 1259 OID 16459)
-- Name: fki_fk_user_id; Type: INDEX; Schema: news_manager; Owner: postgres
--

CREATE INDEX fki_fk_user_id ON news_manager.user_role USING btree (user_id);


--
-- TOC entry 3200 (class 2606 OID 16433)
-- Name: news_author author_id_fk; Type: FK CONSTRAINT; Schema: news_manager; Owner: postgres
--

ALTER TABLE ONLY news_manager.news_author
    ADD CONSTRAINT author_id_fk FOREIGN KEY (author_id) REFERENCES news_manager.author(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3198 (class 2606 OID 16454)
-- Name: user_role fk_user_id; Type: FK CONSTRAINT; Schema: news_manager; Owner: postgres
--

ALTER TABLE ONLY news_manager.user_role
    ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES news_manager."user"(id) ON UPDATE CASCADE ON DELETE CASCADE NOT VALID;


--
-- TOC entry 3199 (class 2606 OID 16428)
-- Name: news_author news_id_fk; Type: FK CONSTRAINT; Schema: news_manager; Owner: postgres
--

ALTER TABLE ONLY news_manager.news_author
    ADD CONSTRAINT news_id_fk FOREIGN KEY (news_id) REFERENCES news_manager.news(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3201 (class 2606 OID 16441)
-- Name: news_tag news_id_fk; Type: FK CONSTRAINT; Schema: news_manager; Owner: postgres
--

ALTER TABLE ONLY news_manager.news_tag
    ADD CONSTRAINT news_id_fk FOREIGN KEY (news_id) REFERENCES news_manager.news(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3202 (class 2606 OID 16446)
-- Name: news_tag tag_id_fk; Type: FK CONSTRAINT; Schema: news_manager; Owner: postgres
--

ALTER TABLE ONLY news_manager.news_tag
    ADD CONSTRAINT tag_id_fk FOREIGN KEY (tag_id) REFERENCES news_manager.tag(id) ON UPDATE CASCADE ON DELETE CASCADE;


-- Completed on 2022-07-26 19:15:26

--
-- PostgreSQL database dump complete
--

