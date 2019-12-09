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

    private static final String SELECTION = "SELECT * FROM students";
    private static final String SELECTION_BY_COURSE_NAME =
            "SELECT students.student_id, students.group_id, students.first_name, students.last_name " + 
            "FROM courses_students " + 
            "INNER  JOIN students " + 
            "ON courses_students.student_id = students.student_id " + 
            "INNER  JOIN courses " + 
            "ON courses_students.course_id = courses.course_id " + 
            "WHERE courses.course_name = ?" +
            "ORDER BY students.student_id";
    private static final int SELECTION_BY_COURSE_NAME_PARAMETER = 1;    
    private static final String SELECTION_FIELD_NAME_STUDENT_ID = "student_id";
    private static final String SELECTION_FIELD_NAME_FIRST_NAME = "first_name";
    private static final String SELECTION_FIELD_NAME_LAST_NAME = "last_name";
    private static final String SELECTION_FIELD_NAME_GROUP_ID = "group_id";

    private static final String INSERTION = "INSERT INTO students (student_id, group_id, first_name, last_name) VALUES (?, ?, ?, ?)";
    private static final int INSERTION_STUDENT_ID_PARAMETER = 1;
    private static final int INSERTION_GROUP_ID_PARAMETER = 2;
    private static final int INSERTION_FIRST_NAME_PARAMETER = 3;
    private static final int INSERTION_LAST_NAME_PARAMETER = 4;

    private static final String INSERTION_GENERATE_ID = "INSERT INTO students (group_id, first_name, last_name) VALUES (?, ?, ?)";
    private static final int INSERTION_GENERATE_ID_GROUP_ID_PARAMETER = 1;
    private static final int INSERTION_GENERATE_ID_FIRST_NAME_PARAMETER = 2;
    private static final int INSERTION_GENERATE_ID_LAST_NAME_PARAMETER = 3;

    private static final String DELETION = "DELETE FROM students WHERE student_id = ?";
    private static final int DELETION_PARAMETER_STUDENT_ID = 1;

    private static final String ASSIGNMENT = "INSERT INTO courses_students (student_id, course_id) VALUES (?, ?)";
    private static final int ASSIGNMENT_STUDENT_ID_PARAMETER = 1;
    private static final int ASSIGNMENT_COURSE_ID_PARAMETER = 2;

    private static final String EXPULSION =
            "DELETE FROM courses_students WHERE student_id = ? AND course_id = ?";
    private static final int EXPULSION_STUDENT_ID_PARAMETER = 1;
    private static final int EXPULSION_COURSE_ID_PARAMETER = 2;

    private final static Logger logger = Logger.getLogger(StudentDaoJdbc.class);

    @Override
    public List<Student> findAll() {

        List<Student> students = new ArrayList<Student>();

        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECTION);
                ResultSet resultSet = statement.executeQuery();) {
            students = processResultSet(resultSet);
            connection.close();
        } catch (SQLException e) {
            logger.error("Cannot retireve students", e);
        }

        return students;
    }

    private List<Student> processResultSet(ResultSet resultSet) throws SQLException {

        List<Student> students = new ArrayList<>();

        while (resultSet.next()) {
            Student student = new Student(resultSet.getInt(SELECTION_FIELD_NAME_STUDENT_ID),
                    resultSet.getString(SELECTION_FIELD_NAME_FIRST_NAME),
                    resultSet.getString(SELECTION_FIELD_NAME_LAST_NAME));
            student.setGroupId(resultSet.getInt(SELECTION_FIELD_NAME_GROUP_ID));
            students.add(student);
        }

        return students;

    }

    @Override
    public void saveAll(List<Student> students) {
        students.forEach(s -> save(s));
    }

    @Override
    public void save(Student student) {
        
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = prepareSaveStatement(connection, student)) {
            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            logger.error("Cannot retireve students", e);
        }

    }

    private PreparedStatement prepareSaveStatement(Connection connection, Student student) throws SQLException {
        PreparedStatement statement;
        if (student.getId() == 0) {
            statement = connection.prepareStatement(INSERTION_GENERATE_ID);
            statement.setInt(INSERTION_GENERATE_ID_GROUP_ID_PARAMETER, student.getGroupId());
            statement.setString(INSERTION_GENERATE_ID_FIRST_NAME_PARAMETER, student.getFirstName());
            statement.setString(INSERTION_GENERATE_ID_LAST_NAME_PARAMETER, student.getLastName());
        } else {
            statement = connection.prepareStatement(INSERTION);
            statement.setInt(INSERTION_STUDENT_ID_PARAMETER, student.getId());
            statement.setInt(INSERTION_GROUP_ID_PARAMETER, student.getGroupId());
            statement.setString(INSERTION_FIRST_NAME_PARAMETER, student.getFirstName());
            statement.setString(INSERTION_LAST_NAME_PARAMETER, student.getLastName());
        }
        return statement;
    }

    @Override
    public void deleteById(int studentId) {

        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(DELETION)) {
            statement.setInt(DELETION_PARAMETER_STUDENT_ID, studentId);
            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            logger.error("Cannot delete student by ID", e);
        }
    }

    @Override
    public List<Student> findByCourseName(String courseName) {

        List<Student> students = new ArrayList<Student>();

        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECTION_BY_COURSE_NAME)) {
            statement.setString(SELECTION_BY_COURSE_NAME_PARAMETER, courseName);
            ResultSet resultSet = statement.executeQuery();
            students = processResultSet(resultSet);
            connection.close();
        } catch (SQLException e) {
            logger.error("Cannot retireve students by course name", e);
        }

        return students;
    }

    @Override
    public void assignToCourse(int studentId, int courseId) {

        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(ASSIGNMENT)) {
            statement.setInt(ASSIGNMENT_STUDENT_ID_PARAMETER, studentId);
            statement.setInt(ASSIGNMENT_COURSE_ID_PARAMETER, courseId);
            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            logger.error("Cannot assign a student to a course", e);
        }
        
    }

    @Override
    public void deleteFromCourse(int studentId, int courseId) {
        
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(EXPULSION)) {
            statement.setInt(EXPULSION_STUDENT_ID_PARAMETER, studentId);
            statement.setInt(EXPULSION_COURSE_ID_PARAMETER, courseId);
            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            logger.error(String.format("Cannot delete the student ID:%s from the course ID:%s", studentId, courseId),
                    e);
        }
    }

}
