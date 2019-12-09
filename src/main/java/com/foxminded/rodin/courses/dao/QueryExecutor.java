package com.foxminded.rodin.courses.dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class QueryExecutor {

    private static final String PATH_TO_CREATE_USER_QUERY = "src/main/resources/create-user.sql";
    private static final String PATH_TO_CREATE_DB_QUERY = "src/main/resources/create-db-courses.sql";
    private static final String PATH_TO_DROP_DB_QUERY = "src/main/resources/drop-db-courses.sql";
    private static final String PATH_TO_CREATE_TABLE_COURSES_QUERY = "src/main/resources/create-table-courses.sql";
    private static final String PATH_TO_CREATE_TABLE_GROUPS_QUERY = "src/main/resources/create-table-groups.sql";
    private static final String PATH_TO_CREATE_TABLE_STUDENTS_QUERY = "src/main/resources/create-table-students.sql";
    private static final String PATH_TO_CREATE_TABLE_COURSES_QUERY_STUDENTS_QUERY = "src/main/resources/create-table-courses_students.sql";

    private static final String UTF8 = "UTF-8";

    private final static Logger logger = Logger.getLogger(QueryExecutor.class);

    public static void createDatabase() {

        try {
            executeQuery(computeQueryText(PATH_TO_CREATE_DB_QUERY));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteDatabase() {

        try {
            executeQuery(computeQueryText(PATH_TO_DROP_DB_QUERY));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createUser() {

        try {
            executeQuery(computeQueryText(PATH_TO_CREATE_USER_QUERY));

        } catch (Exception e) {
            logger.error("Cannot create table database user", e);
        }
    }

    public static void createTables() {
        try {
            executeQuery(computeQueryText(PATH_TO_CREATE_TABLE_COURSES_QUERY));
            executeQuery(computeQueryText(PATH_TO_CREATE_TABLE_GROUPS_QUERY));
            executeQuery(computeQueryText(PATH_TO_CREATE_TABLE_STUDENTS_QUERY));
            executeQuery(computeQueryText(PATH_TO_CREATE_TABLE_COURSES_QUERY_STUDENTS_QUERY));
        } catch (Exception e) {
            logger.error("Cannot create database tables database", e);
        }
    }

    private static void executeQuery(String queryText) {

        try (Connection connection = ConnectionUtils.getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute(queryText);
        } catch (Exception e) {
            logger.error("Cannot execute query", e);
        }
    }

    private static String computeQueryText(String filePath) throws IOException {
        FileInputStream inputFile = new FileInputStream(filePath);
        return computeQueryFileContent(inputFile, UTF8);
    }

    private static String computeQueryFileContent(FileInputStream inputStream, String encoding) throws IOException {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, encoding));
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
                contentBuilder.append('\n');
            }
            return contentBuilder.toString();
        }


}
