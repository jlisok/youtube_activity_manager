--liquibase formatted sql

--changeset jlisok:1
CREATE TYPE public.synchronization_state AS ENUM (
    'SUCCEEDED',
    'IN_PROGRESS',
    'FAILED'
);

CREATE TABLE public.synchronization_statuses (
    id uuid PRIMARY KEY NOT NULL,
    status synchronization_state NOT NULL,
    user_id uuid NOT NULL,
    modified_at timestamp without time zone DEFAULT now() NOT NULL
);

ALTER TABLE public.synchronization_statuses
    ADD CONSTRAINT videos_users_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;


--rollback ALTER TABLE public.videos DROP CONSTRAINT videos_users_user_id_fkey;
--rollback DROP TABLE public.synchronization_statuses CASCADE;
--rollback DROP TYPE public.SynchronizationState;


--changeset jlisok:2
ALTER TABLE public.users_channels
    DROP COLUMN id,
    DROP COLUMN modified_at,
    ADD PRIMARY KEY (user_id, channel_id);

--rollback CREATE EXTENSION uuid-ossp;
--rollback ALTER TABLE public.users_channels DROP PRIMARY KEY, ADD id uuid PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(), ADD modified_at timestamp without time zone DEFAULT now() NOT NULL;
--rollback DROP EXTENSION uuid-ossp;


--changeset jlisok:3
ALTER TABLE public.channels
    ADD CONSTRAINT youtube_channel_id_unique UNIQUE (youtube_channel_id);

ALTER TABLE public.videos
    ADD CONSTRAINT youtube_video_id_unique UNIQUE (youtube_video_id);

--rollback ALTER TABLE public.videos DROP CONSTRAINT youtube_video_id_unique;
--rollback ALTER TABLE public.channels DROP CONSTRAINT youtube_channel_id_unique;
