--liquibase formatted sql

--changeset jlisok:1
CREATE TABLE public.channels (
    id uuid PRIMARY KEY NOT NULL,
    youtube_channel_id text NOT NULL,
    title text NOT NULL,
    owner text,
    published_at timestamp without time zone NOT NULL,
    language text,
    country text,
    number_of_views bigint NOT NULL,
    number_of_subscribers bigint NOT NULL,
    number_of_videos integer NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    modified_at timestamp without time zone DEFAULT now() NOT NULL
);

CREATE TABLE public.users_channels (
    id uuid PRIMARY KEY NOT NULL,
    channel_id uuid NOT NULL,
    user_id uuid NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    modified_at timestamp without time zone DEFAULT now() NOT NULL
);

ALTER TABLE public.users_channels
    ADD CONSTRAINT channel_id_fkey_channels FOREIGN KEY (channel_id) REFERENCES public.channels(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;

ALTER TABLE public.users_channels
    ADD CONSTRAINT user_id_fkey_users FOREIGN KEY (user_id) REFERENCES public.users(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;


--rollback ALTER TABLE public.users_channels DROP CONSTRAINT user_id_fkey_users;
--rollback ALTER TABLE public.users_channels DROP CONSTRAINT channel_id_fkey_channels;
--rollback DROP TABLE public.users_channels CASCADE;
--rollback DROP TABLE public.channels CASCADE;


--changeset jlisok:2

ALTER TABLE public.videos
    DROP COLUMN channel_id;

ALTER TABLE public.videos
    ADD channel_id uuid NOT NULL;

ALTER TABLE public.videos
    ADD CONSTRAINT channel_id_fkey_channels FOREIGN KEY (channel_id) REFERENCES public.channels(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;

--rollback ALTER TABLE public.videos DROP CONSTRAINT channel_id_fkey_channels;
--rollback ALTER TABLE public.videos DROP COLUMN channel_id;
--rollback ALTER TABLE public.videos ADD channel_id text NOT NULL;



--changeset jlisok:3
ALTER TABLE public.users
    ADD UNIQUE (google_id);

--rollback ALTER TABLE public.users ALTER COLUMN google_id SET NULL;

