--liquibase formatted sql

--changeset jlisok:1
CREATE TABLE public.video_categories (
    id uuid PRIMARY KEY NOT NULL,
    youtube_id text UNIQUE NOT NULL,
    category_name text NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    modified_at timestamp without time zone DEFAULT now() NOT NULL
);

CREATE INDEX video_categories_youtube_id_idx ON public.video_categories (youtube_id);

INSERT INTO public.video_categories (id, youtube_id, category_name, created_at, modified_at) VALUES ('8a2b58ae-41c5-46d1-b655-38bd25ed306b', 'NO_CATEGORY', 'NO_CATEGORY', now(), now());

ALTER TABLE public.videos
    ADD video_category_id uuid NOT NULL DEFAULT '8a2b58ae-41c5-46d1-b655-38bd25ed306b',
    ADD CONSTRAINT video_category_id_fkey FOREIGN KEY (video_category_id) REFERENCES public.video_categories(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;


--rollback ALTER TABLE public.users_channels DROP CONSTRAINT video_category_id_fkey;
--rollback DROP INDEX yt_id_idx;
--rollback DROP TABLE public.videos_video_categories CASCADE;
