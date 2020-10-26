--liquibase formatted sql

--changeset jlisok:1
CREATE TYPE public.rating AS ENUM (
    'LIKE',
    'DISLIKE'
);

CREATE TABLE public.videos (
    id uuid PRIMARY KEY NOT NULL,
    title text NOT NULL,
    channel_id uuid NOT NULL,
    youtube_video_id text UNIQUE NOT NULL,
    duration interval NOT NULL,
    published_at timestamp without time zone NOT NULL,
    hashtag text[],
    uri text[],
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    modified_at timestamp without time zone DEFAULT now() NOT NULL
);



CREATE TABLE public.users_videos (
    id uuid PRIMARY KEY NOT NULL,
    user_id uuid NOT NULL,
    video_id uuid NOT NULL,
    rating public.rating NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    modified_at timestamp without time zone DEFAULT now() NOT NULL
);

ALTER TABLE public.users_videos
    ADD CONSTRAINT video_id_fkey_users FOREIGN KEY (user_id) REFERENCES public.users(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;

ALTER TABLE public.users_videos
    ADD CONSTRAINT video_id_fkey_videos FOREIGN KEY (video_id) REFERENCES public.videos(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;

ALTER TABLE public.users_videos
    ADD CONSTRAINT video_id_user_id_unique_users_videos UNIQUE (user_id, video_id);


--rollback ALTER TABLE public.users_videos DROP CONSTRAINT video_id_user_id_unique_users_videos;
--rollback DROP TABLE public.videos CASCADE;
--rollback DROP TABLE public.users_videos CASCADE;
--rollback DROP TYPE public.rating;


