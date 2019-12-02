package com.foxminded.rodin.courses.dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class QueryExecutor {

    private static final String QUERY_FILE_PATH_CREATE_USER = "src/main/resources/create-user.sql";
    private static final String QUERY_FILE_PATH_CREATE_DB = "src/main/resources/create-db-courses.sql";
    private static final String QUERY_FILE_PATH_DROP_DB = "src/main/resources/drop-db-courses.sql";
    private static final String QUERY_FILE_PATH_CREATE_TABLE_COURSES = "src/main/resources/create-table-courses.sql";
    private static final String QUERY_FILE_PATH_CREATE_TABLE_GROUPS = "src/main/resources/create-table-groups.sql";
    private static final String QUERY_FILE_PATH_CREATE_TABLE_STUDENTS = "src/main/resources/create-table-students.sql";
    private static final String QUERY_FILE_PATH_CREATE_TABLE_COURSES_STUDENTS = "src/main/resources/create-table-courses_students.sql";

    private final static Logger logger = Logger.getLogger(QueryExecutor.class);

    public static void createDatabase() {

        try {
            executeQuery(computeQueryText(QUERY_FILE_PATH_CREATE_DB));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteDatabase() {

        try {
            executeQuery(computeQueryText(QUERY_FILE_PATH_DROP_DB));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createUser() {

        try {
            executeQuery(computeQueryText(QUERY_FILE_PATH_CREATE_USER));

        } catch (Exception e) {
            logger.error("Cannot create table database user", e);
        }
    }

    public static void createTables() {
        try {
            executeQuery(computeQueryText(QUERY_FILE_PATH_CREATE_TABLE_COURSES));
            executeQuery(computeQueryText(QUERY_FILE_PATH_CREATE_TABLE_GROUPS));
            executeQuery(computeQueryText(QUERY_FILE_PATH_CREATE_TABLE_STUDENTS));
            executeQuery(computeQueryText(QUERY_FILE_PATH_CREATE_TABLE_COURSES_STUDENTS));
        } catch (Exception e) {
            logger.error("Cannot create database tables database", e);
        }
    }

    private static void executeQuery(String queryText) {

        Connection connection = null;

        try {
            connection = ConnectionUtils.getConnection();
        } catch (Exception e) {
            logger.error("Cannot establish connection", e);
            return;
        }

        try {
            Statement statement = connection.createStatement();
            statement.execute(queryText);
        } catch (Exception e) {
            logger.error("Cannot execute query", e);
        } finally {
            ConnectionUtils.closeQuietly(connection);
        }
    }

    private static String computeQueryText(String filePath) throws IOException {
        FileInputStream inputFile = new FileInputStream(filePath);
        String sqlDropCreateDatabase = computeQueryFileContent(inputFile, "UTF-8");
        return sqlDropCreateDatabase;
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
