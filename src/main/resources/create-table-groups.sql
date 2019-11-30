-- Table: public.groups

ALTER DATABASE courses;

DROP TABLE IF EXISTS public.groups;

CREATE TABLE public.groups
(
    group_id integer NOT NULL,
    name character varying(100) NOT NULL,
    CONSTRAINT groups_pkey PRIMARY KEY (group_id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.groups
    OWNER to courses_admin;