--
-- PostgreSQL database dump
--

-- Dumped from database version 12.3
-- Dumped by pg_dump version 12.3

-- Started on 2020-07-27 13:33:04

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
-- TOC entry 4 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- TOC entry 2903 (class 0 OID 0)
-- Dependencies: 4
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- TOC entry 641 (class 1247 OID 16492)
-- Name: opinion; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.opinion AS ENUM (
    'like',
    'dislike'
);


ALTER TYPE public.opinion OWNER TO postgres;

--
-- TOC entry 638 (class 1247 OID 16418)
-- Name: sex; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.sex AS ENUM (
    'male',
    'female',
    'not applicable'
);


ALTER TYPE public.sex OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 205 (class 1259 OID 16590)
-- Name: channel_categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.channel_categories (
    id uuid NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.channel_categories OWNER TO postgres;

--
-- TOC entry 206 (class 1259 OID 16598)
-- Name: channels; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.channels (
    id uuid NOT NULL,
    name text NOT NULL,
    content_creator text NOT NULL,
    number_of_subscribers integer,
    number_of_videos integer NOT NULL,
    active_since timestamp without time zone NOT NULL,
    category_id uuid NOT NULL,
    language text NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    modified_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.channels OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 16575)
-- Name: user_personal_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_personal_data (
    id uuid NOT NULL,
    gender public.sex NOT NULL,
    birth_year integer NOT NULL,
    country text NOT NULL,
    phone_number text,
    first_name text,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    modified_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.user_personal_data OWNER TO postgres;

--
-- TOC entry 203 (class 1259 OID 16559)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id uuid NOT NULL,
    password text,
    email text NOT NULL,
    facebook_id uuid,
    google_id uuid,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    modified_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 207 (class 1259 OID 16613)
-- Name: users_channels; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users_channels (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    channel_id uuid NOT NULL
);


ALTER TABLE public.users_channels OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 16641)
-- Name: users_videos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users_videos (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    video_id uuid NOT NULL,
    like_dislike public.opinion NOT NULL
);


ALTER TABLE public.users_videos OWNER TO postgres;

--
-- TOC entry 208 (class 1259 OID 16628)
-- Name: videos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.videos (
    id uuid NOT NULL,
    channel_id uuid NOT NULL,
    title text NOT NULL,
    duration interval NOT NULL,
    publish_date timestamp without time zone NOT NULL,
    hashtags text[] NOT NULL,
    tags text[] NOT NULL,
    links text[] NOT NULL
);


ALTER TABLE public.videos OWNER TO postgres;

--
-- TOC entry 2750 (class 2606 OID 16677)
-- Name: channel_categories channel_categories_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channel_categories
    ADD CONSTRAINT channel_categories_name_key UNIQUE (name);


--
-- TOC entry 2752 (class 2606 OID 16597)
-- Name: channel_categories channel_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channel_categories
    ADD CONSTRAINT channel_categories_pkey PRIMARY KEY (id);


--
-- TOC entry 2755 (class 2606 OID 16607)
-- Name: channels channels_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channels
    ADD CONSTRAINT channels_pkey PRIMARY KEY (id);


--
-- TOC entry 2748 (class 2606 OID 16584)
-- Name: user_personal_data user_personal_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_personal_data
    ADD CONSTRAINT user_personal_data_pkey PRIMARY KEY (id);


--
-- TOC entry 2758 (class 2606 OID 16617)
-- Name: users_channels users_channels_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users_channels
    ADD CONSTRAINT users_channels_pkey PRIMARY KEY (id);


--
-- TOC entry 2738 (class 2606 OID 16570)
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 2740 (class 2606 OID 16572)
-- Name: users users_facebook_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_facebook_id_key UNIQUE (facebook_id);


--
-- TOC entry 2742 (class 2606 OID 16574)
-- Name: users users_google_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_google_id_key UNIQUE (google_id);


--
-- TOC entry 2745 (class 2606 OID 16568)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 2764 (class 2606 OID 16645)
-- Name: users_videos users_videos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users_videos
    ADD CONSTRAINT users_videos_pkey PRIMARY KEY (id);


--
-- TOC entry 2761 (class 2606 OID 16635)
-- Name: videos videos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.videos
    ADD CONSTRAINT videos_pkey PRIMARY KEY (id);


--
-- TOC entry 2753 (class 1259 OID 16709)
-- Name: channels_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX channels_index ON public.channels USING btree (id);


--
-- TOC entry 2746 (class 1259 OID 16708)
-- Name: user_personal_data_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX user_personal_data_index ON public.user_personal_data USING btree (id);


--
-- TOC entry 2756 (class 1259 OID 16706)
-- Name: users_channels_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX users_channels_index ON public.users_channels USING btree (user_id, channel_id);


--
-- TOC entry 2743 (class 1259 OID 16707)
-- Name: users_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX users_index ON public.users USING btree (id);


--
-- TOC entry 2762 (class 1259 OID 16705)
-- Name: users_videos_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX users_videos_index ON public.users_videos USING btree (user_id, video_id, like_dislike);


--
-- TOC entry 2759 (class 1259 OID 16704)
-- Name: videos_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX videos_index ON public.videos USING btree (id, channel_id);


--
-- TOC entry 2766 (class 2606 OID 16608)
-- Name: channels channels_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channels
    ADD CONSTRAINT channels_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.channel_categories(id);


--
-- TOC entry 2765 (class 2606 OID 16667)
-- Name: user_personal_data user_personal_data_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_personal_data
    ADD CONSTRAINT user_personal_data_id_fkey FOREIGN KEY (id) REFERENCES public.users(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2767 (class 2606 OID 16678)
-- Name: users_channels users_channels_channels_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users_channels
    ADD CONSTRAINT users_channels_channels_id_fkey FOREIGN KEY (channel_id) REFERENCES public.channels(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2768 (class 2606 OID 16683)
-- Name: users_channels users_channels_users_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users_channels
    ADD CONSTRAINT users_channels_users_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2770 (class 2606 OID 16688)
-- Name: users_videos users_videos_users_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users_videos
    ADD CONSTRAINT users_videos_users_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 2771 (class 2606 OID 16693)
-- Name: users_videos users_videos_videos_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users_videos
    ADD CONSTRAINT users_videos_videos_id_fkey FOREIGN KEY (video_id) REFERENCES public.videos(id) ON DELETE CASCADE;


--
-- TOC entry 2769 (class 2606 OID 16636)
-- Name: videos videos_channels_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.videos
    ADD CONSTRAINT videos_channels_id_fkey FOREIGN KEY (channel_id) REFERENCES public.channels(id);


-- Completed on 2020-07-27 13:33:04

--
-- PostgreSQL database dump complete
--

