--liquibase formatted sql

--changeset jlisok:1
CREATE TYPE public.synchronization_state AS ENUM (
    'SUCCEEDED',
    'IN_PROGRESS',
    'FAILED'
);

CREATE TABLE public.synchronization_statuses (
    id uuid PRIMARY KEY NOT NULL,
    state synchronization_state NOT NULL,
    user_id uuid NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);

ALTER TABLE public.synchronization_statuses
    ADD CONSTRAINT videos_users_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;


--rollback ALTER TABLE public.videos DROP CONSTRAINT videos_users_user_id_fkey;
--rollback DROP TABLE public.synchronization_statuses CASCADE;
--rollback DROP TYPE public.synchronization_state;
