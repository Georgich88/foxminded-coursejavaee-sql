-- Table: public.courses_students

ALTER DATABASE courses;

DROP TABLE IF EXISTS public.courses_students;

CREATE TABLE public.courses_students
(
    course_id integer NOT NULL,
    student_id integer NOT NULL,
    CONSTRAINT courses_students_pkey PRIMARY KEY (course_id, student_id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.courses_students
    OWNER to courses_admin;