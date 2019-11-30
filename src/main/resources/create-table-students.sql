-- Table: public.students

ALTER DATABASE courses;

DROP TABLE IF EXISTS public.students;

CREATE TABLE public.students
(
    student_id integer NOT NULL,
    group_id integer,
    first_name character varying(100) NOT NULL,
    last_name character varying(100) NOT NULL,
    CONSTRAINT students_pkey PRIMARY KEY (student_id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.students
    OWNER to courses_admin;