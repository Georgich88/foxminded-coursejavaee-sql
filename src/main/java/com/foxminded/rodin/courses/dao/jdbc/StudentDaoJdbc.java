package com.foxminded.rodin.courses.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.foxminded.rodin.courses.dao.ConnectionUtils;
import com.foxminded.rodin.courses.dao.StudentDao;
import com.foxminded.rodin.courses.domain.Student;

public class StudentDaoJdbc implements StudentDao {

    private static final String SELECT_ALL = "SELECT * FROM students";
    private static final String SELECT_BY_COURSE_NAME =
            "SELECT students.student_id, students.group_id, students.first_name, students.last_name " + 
            "FROM courses_students " + 
            "INNER  JOIN students " + 
            "ON courses_students.student_id = students.student_id " + 
            "INNER  JOIN courses " + 
            "ON courses_students.course_id = courses.course_id " + 
            "WHERE courses.course_name = ?" +
            "ORDER BY students.student_id";
    private static final int SELECT_BY_COURSE_NAME_ARG_NUMBER = 1;    
    private static final String SELECT_FIELD_NAME_STUDENT_ID = "student_id";
    private static final String SELECT_FIELD_NAME_FIRST_NAME = "first_name";
    private static final String SELECT_FIELD_NAME_LAST_NAME = "last_name";
    private static final String SELECT_FIELD_NAME_GROUP_ID = "group_id";

    private static final String INSERT = "INSERT INTO students (student_id, group_id, first_name, last_name) VALUES (?, ?, ?, ?)";
    private static final int INSERT_ARG_NUMBER_STUDENT_ID = 1;
    private static final int INSERT_ARG_NUMBER_GROUP_ID = 2;
    private static final int INSERT_ARG_NUMBER_FIRST_NAME = 3;
    private static final int INSERT_ARG_NUMBER_LAST_NAME = 4;

    private static final String INSERT_GENERATE_ID = "INSERT INTO students (group_id, first_name, last_name) VALUES (?, ?, ?)";
    private static final int INSERT_GENERATE_ID_ARG_NUMBER_GROUP_ID = 1;
    private static final int INSERT_GENERATE_ID_ARG_NUMBER_FIRST_NAME = 2;
    private static final int INSERT_GENERATE_ID_ARG_NUMBER_LAST_NAME = 3;

    private static final String DELETE = "DELETE FROM students WHERE student_id = ?";
    private static final int DELETE_ARG_NUMBER_STUDENT_ID = 1;

    private static final String ASSIGN_TO_COURSE = "INSERT INTO courses_students (student_id, course_id) VALUES (?, ?)";
    private static final int ASSIGN_TO_COURSE_ARG_NUMBER_STUDENT_ID = 1;
    private static final int ASSIGN_TO_COURSE_ARG_NUMBER_COURSE_ID = 2;

    private static final String DELETE_FROM_COURSE =
            "DELETE FROM courses_students WHERE student_id = ? AND course_id = ?";
    private static final int DELETE_FROM_COURSE_ARG_NUMBER_STUDENT_ID = 1;
    private static final int DELETE_FROM_COURSE_ARG_NUMBER_COURSE_ID = 2;

    private final static Logger logger = Logger.getLogger(StudentDaoJdbc.class);

    @Override
    public List<Student> findAll() {

        List<Student> students = new ArrayList<Student>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionUtils.getConnection();
        } catch (Exception e) {
            logger.error("Cannot establish connection", e);
            return students;
        }

        try {
            statement = connection.prepareStatement(SELECT_ALL);
            resultSet = statement.executeQuery();
            students = processResultSet(resultSet);
        } catch (SQLException e) {
            logger.error("Cannot retireve students", e);
        } finally {
            ConnectionUtils.closeQuietly(resultSet);
            ConnectionUtils.closeQuietly(statement);
            ConnectionUtils.closeQuietly(connection);
        }

        return students;
    }

    private List<Student> processResultSet(ResultSet resultSet) throws SQLException {

        List<Student> students = new ArrayList<>();

        while (resultSet.next()) {
            Student student = new Student(resultSet.getInt(SELECT_FIELD_NAME_STUDENT_ID),
                    resultSet.getString(SELECT_FIELD_NAME_FIRST_NAME),
                    resultSet.getString(SELECT_FIELD_NAME_LAST_NAME));
            student.setGroupId(resultSet.getInt(SELECT_FIELD_NAME_GROUP_ID));
            students.add(student);
        }

        return students;

    }

    @Override
    public void saveAll(List<Student> students) {

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ConnectionUtils.getConnection();
        } catch (Exception e) {
            logger.error("Cannot establish connection", e);
            return;
        }

        try {
            for (Student student : students) {
                statement = prepareSaveStatement(connection, student);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            logger.error("Cannot retireve students", e);
        } finally {
            ConnectionUtils.closeQuietly(statement);
            ConnectionUtils.closeQuietly(connection);
        }
        
    }

    @Override
    public void save(Student student) {
        
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ConnectionUtils.getConnection();
        } catch (Exception e) {
            logger.error("Cannot establish connection", e);
            return;
        }

        try {
            statement = prepareSaveStatement(connection, student);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Cannot retireve students", e);
        } finally {
            ConnectionUtils.closeQuietly(statement);
            ConnectionUtils.closeQuietly(connection);
        }

    }

    private PreparedStatement prepareSaveStatement(Connection connection, Student student) throws SQLException {
        PreparedStatement statement;
        if (student.getId() == 0) {
            statement = connection.prepareStatement(INSERT_GENERATE_ID);
            statement.setInt(INSERT_GENERATE_ID_ARG_NUMBER_GROUP_ID, student.getGroupId());
            statement.setString(INSERT_GENERATE_ID_ARG_NUMBER_FIRST_NAME, student.getFirstName());
            statement.setString(INSERT_GENERATE_ID_ARG_NUMBER_LAST_NAME, student.getLastName());
        } else {
            statement = connection.prepareStatement(INSERT);
            statement.setInt(INSERT_ARG_NUMBER_STUDENT_ID, student.getId());
            statement.setInt(INSERT_ARG_NUMBER_GROUP_ID, student.getGroupId());
            statement.setString(INSERT_ARG_NUMBER_FIRST_NAME, student.getFirstName());
            statement.setString(INSERT_ARG_NUMBER_LAST_NAME, student.getLastName());
        }
        return statement;
    }

    @Override
    public void deleteById(int studentId) {

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ConnectionUtils.getConnection();
        } catch (Exception e) {
            logger.error("Cannot establish connection", e);
            return;
        }

        try {
            statement = connection.prepareStatement(DELETE);
            statement.setInt(DELETE_ARG_NUMBER_STUDENT_ID, studentId);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Cannot delete student by ID", e);
        } finally {
            ConnectionUtils.closeQuietly(statement);
            ConnectionUtils.closeQuietly(connection);
        }
    }

    @Override
    public List<Student> findByCourseName(String courseName) {

        List<Student> students = new ArrayList<Student>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionUtils.getConnection();
        } catch (Exception e) {
            logger.error("Cannot establish connection", e);
            return students;
        }

        try {
            statement = connection.prepareStatement(SELECT_BY_COURSE_NAME);
            statement.setString(SELECT_BY_COURSE_NAME_ARG_NUMBER, courseName);
            resultSet = statement.executeQuery();
            students = processResultSet(resultSet);
        } catch (SQLException e) {
            logger.error("Cannot retireve students by course name", e);
        } finally {
            ConnectionUtils.closeQuietly(resultSet);
            ConnectionUtils.closeQuietly(statement);
            ConnectionUtils.closeQuietly(connection);
        }

        return students;
    }

    @Override
    public void assignToCourse(int studentId, int courseId) {

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ConnectionUtils.getConnection();
        } catch (Exception e) {
            logger.error("Cannot establish connection", e);
            return;
        }

        try {
            statement = connection.prepareStatement(ASSIGN_TO_COURSE);
            statement.setInt(ASSIGN_TO_COURSE_ARG_NUMBER_STUDENT_ID, studentId);
            statement.setInt(ASSIGN_TO_COURSE_ARG_NUMBER_COURSE_ID, courseId);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Cannot assign a student to a course", e);
        } finally {
            ConnectionUtils.closeQuietly(statement);
            ConnectionUtils.closeQuietly(connection);
        }
        
    }

    @Override
    public void deleteFromCourse(int studentId, int courseId) {
        
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ConnectionUtils.getConnection();
        } catch (Exception e) {
            logger.error("Cannot establish connection", e);
            return;
        }
        logger.trace(String.format("Cannot delete the student ID:%s from the course ID:%s", studentId, courseId));
        try {
            statement = connection.prepareStatement(DELETE_FROM_COURSE);
            statement.setInt(DELETE_FROM_COURSE_ARG_NUMBER_STUDENT_ID, studentId);
            statement.setInt(DELETE_FROM_COURSE_ARG_NUMBER_COURSE_ID, courseId);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error(String.format("Cannot delete the student ID:%s from the course ID:%s", studentId, courseId),
                    e);
        } finally {
            ConnectionUtils.closeQuietly(statement);
            ConnectionUtils.closeQuietly(connection);
        }
    }

}
