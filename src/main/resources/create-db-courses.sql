-- Database: courses

DO
$do$
BEGIN
   IF NOT EXISTS (
      SELECT
      FROM   pg_catalog.pg_database
      WHERE  datname = 'courses') THEN
      CREATE DATABASE courses
	    WITH 
	    OWNER = courses_admin
	    ENCODING = 'UTF8'
	    LC_COLLATE = 'English_United States.1252'
	    LC_CTYPE = 'English_United States.1252'
	    TABLESPACE = pg_default
	    CONNECTION LIMIT = -1;
	
		COMMENT ON DATABASE courses
		    IS 'Foxminded JavaEE Course Task 7 - SQL';
   END IF;
END
$do$;    