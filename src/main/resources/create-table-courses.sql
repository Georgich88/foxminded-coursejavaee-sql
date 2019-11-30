-- Table: public.courses

ALTER DATABASE courses;

DROP TABLE IF EXISTS public.courses;

CREATE TABLE public.courses
(
    course_id integer NOT NULL,
    course_name character varying(100) NOT NULL,
    course_description character varying(250) NOT NULL,
    CONSTRAINT courses_pkey PRIMARY KEY (course_id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.courses
    OWNER to courses_admin;