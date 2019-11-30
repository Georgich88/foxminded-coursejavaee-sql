package com.foxminded.rodin.courses.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.foxminded.rodin.courses.dao.ConnectionUtils;
import com.foxminded.rodin.courses.dao.StudentDao;
import com.foxminded.rodin.courses.domain.Course;
import com.foxminded.rodin.courses.domain.Student;

public class StudentDaoJdbc implements StudentDao {

    private static final String SELECT_ALL = "SELECT * FROM students";
    private static final String INSERT = "INSERT INTO students (student_id, group_id, first_name, last_name) VALUES (?, ?, ?, ?)";
    private static final String DELETE = "DELETE FROM students WHERE student_id = ?";
    private static final String SELECT_BY_COURSE_NAME =
            "SELECT students.student_id, students.group_id, students.first_name, students.last_name " + 
            "FROM courses_students " + 
            "INNER  JOIN students " + 
            "ON courses_students.student_id = students.student_id " + 
            "INNER  JOIN courses " + 
            "ON courses_students.course_id = courses.course_id " + 
            "WHERE courses.course_name = ?";
    private static final String ASSIGN_TO_COURSE = "INSERT INTO courses_students (student_id, course_id) VALUES (?, ?)";
    private static final String DELETE_FROM_COURSE =
            "DELETE FROM courses_students WHERE student_id = ? AND course_id = ?";
    
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
            Student student = new Student(resultSet.getInt("student_id"), resultSet.getString("first_name"),
                    resultSet.getString("last_name"));
            student.setGroupId(resultSet.getInt("group_id"));
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
            statement = connection.prepareStatement(INSERT);
            for (Student student : students) {
                statement.setInt(1, student.getId());
                statement.setInt(2, student.getGroupId());
                statement.setString(3, student.getFirstName());
                statement.setString(4, student.getLastName());
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
            statement = connection.prepareStatement(INSERT);
            statement.setInt(1, student.getId());
            statement.setInt(2, student.getGroupId());
            statement.setString(3, student.getFirstName());
            statement.setString(4, student.getLastName());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Cannot retireve students", e);
        } finally {
            ConnectionUtils.closeQuietly(statement);
            ConnectionUtils.closeQuietly(connection);
        }

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
            statement.setInt(1, studentId);
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
            statement.setString(1, courseName);
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
    public void assignToCourses(Map<Student, List<Course>> map) {

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
            for (Map.Entry<Student, List<Course>> entry : map.entrySet()) {
                Student student = entry.getKey();
                for (Course course : entry.getValue()) {
                    statement.setInt(1, student.getId());
                    statement.setInt(2, course.getId());
                    statement.addBatch();
                }
            }
            statement.executeBatch();
        } catch (SQLException e) {
            logger.error("Cannot assign a student to courses", e);
        } finally {
            ConnectionUtils.closeQuietly(statement);
            ConnectionUtils.closeQuietly(connection);
        }
        
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
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
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

        try {
            statement = connection.prepareStatement(DELETE_FROM_COURSE);
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Cannot assign a student to a course", e);
        } finally {
            ConnectionUtils.closeQuietly(statement);
            ConnectionUtils.closeQuietly(connection);
        }
    }

}
