--liquibase formatted sql

--changeset jlisok:1
ALTER TABLE public.user_personal_data
    ALTER COLUMN birth_year DROP NOT NULL;

ALTER TABLE public.user_personal_data
    ALTER COLUMN country DROP NOT NULL;

ALTER TABLE public.users
    ADD google_id text NULL;

ALTER TABLE public.users
    ADD google_id_token text NULL;



--rollback ALTER TABLE public.user_personal_data ALTER COLUMN birth_year SET NOT NULL;
--rollback ALTER TABLE public.user_personal_data ALTER COLUMN country SET NOT NULL;
--rollback ALTER TABLE public.users DROP COLUMN google_id;
--rollback ALTER TABLE public.users DROP COLUMN google_id_token;


--changeset jlisok:2
ALTER TABLE public.users
    ADD access_token text NULL;

--rollback ALTER TABLE public.users DROP COLUMN access_token;