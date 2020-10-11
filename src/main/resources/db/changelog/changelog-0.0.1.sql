--liquibase formatted sql

--changeset jlisok:1
CREATE TYPE public.sex AS ENUM (
    'MALE',
    'FEMALE',
    'NOT_APPLICABLE'
);

CREATE TABLE public.user_personal_data (
    id uuid NOT NULL,
    gender public.sex NOT NULL,
    birth_year integer,
    country text,
    phone_number text,
    first_name text,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    modified_at timestamp without time zone DEFAULT now() NOT NULL
);


CREATE TABLE public.users (
    id uuid NOT NULL,
    password text,
    access_token text,
    google_id text,
    google_id_token text,
    email text NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    modified_at timestamp without time zone DEFAULT now() NOT NULL
);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.user_personal_data
    ADD CONSTRAINT user_personal_data_pkey PRIMARY KEY (id);

ALTER TABLE public.users
    ADD CONSTRAINT users_id_fkey FOREIGN KEY (id) REFERENCES public.user_personal_data(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;


--rollback DROP TABLE public.user_personal_data;
--rollback DROP TABLE public.users;
--rollback DROP TYPE public.sex;