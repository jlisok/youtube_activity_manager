--liquibase formatted sql

--changeset jlisok:2

ALTER TABLE public.user_personal_data
    DROP CONSTRAINT user_personal_data_id_fkey;

ALTER TABLE public.users
    ADD CONSTRAINT users_id_fkey FOREIGN KEY (id) REFERENCES public.user_personal_data(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;


--rollback ALTER TABLE public.users DROP CONSTRAINT users_id_fkey;