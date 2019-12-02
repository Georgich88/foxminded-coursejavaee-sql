package com.foxminded.rodin.courses.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.foxminded.rodin.courses.dao.ConnectionUtils;
import com.foxminded.rodin.courses.dao.CourseDao;
import com.foxminded.rodin.courses.domain.Course;
import com.foxminded.rodin.courses.domain.Student;

public class CourseDaoJdbc implements CourseDao {

    private static final String INSERT_COURSES = "INSERT INTO courses (course_id, course_name, course_description) VALUES (?, ?, ?)";
    private static final String INSERT_COURSE_STUDENTS = "INSERT INTO courses_students (course_id, student_id) VALUES (?, ?)";
    private static final String SELECT_ALL_COURSES = "SELECT course_id, course_name, course_description FROM public.courses";
    private static final String SELECT_BY_STUDENT_ID =
            "SELECT courses.course_id, courses.course_name, courses.course_description " + 
            "FROM courses_students " + 
            "INNER JOIN courses " + 
            "ON courses_students.course_id = courses.course_id " + 
            "WHERE courses_students.student_id = ?";
    
    
    private final static Logger logger = Logger.getLogger(CourseDaoJdbc.class);
    
    @Override
    public void saveAll(List<Course> courses) {

        Connection connection = null;
        PreparedStatement statementCourses = null;
        PreparedStatement statementStudents = null;
        try {
            connection = ConnectionUtils.getConnection();
        } catch (Exception e) {
            logger.error("Cannot establish connection", e);
            return;
        }

        try {
            
            statementCourses = connection.prepareStatement(INSERT_COURSES);
            statementStudents = connection.prepareStatement(INSERT_COURSE_STUDENTS); 

            prepareInsertionCoursesStatements(courses, statementCourses, statementStudents);
            
            statementCourses.executeBatch();
            statementStudents.executeBatch();            
            
        } catch (SQLException e) {
            logger.error("Cannot save courses", e);
        } finally {
            ConnectionUtils.closeQuietly(statementCourses);
            ConnectionUtils.closeQuietly(statementStudents);
            ConnectionUtils.closeQuietly(connection);
        }
        
    }

    @Override
    public List<Course> findAll() {
        List<Course> courses = new ArrayList<Course>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionUtils.getConnection();
        } catch (Exception e) {
            logger.error("Cannot establish connection", e);
            return courses;
        }

        try {
            statement = connection.prepareStatement(SELECT_ALL_COURSES);
            resultSet = statement.executeQuery();
            courses = processResultSet(resultSet);
        } catch (SQLException e) {
            logger.error("Cannot save courses", e);
        } finally {
            ConnectionUtils.closeQuietly(resultSet);
            ConnectionUtils.closeQuietly(statement);
            ConnectionUtils.closeQuietly(connection);
        }

        return courses;
    }

    @Override
    public List<Course> findByStudentId(int studentId) {

        List<Course> courses = new ArrayList<Course>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionUtils.getConnection();
        } catch (Exception e) {
            logger.error("Cannot establish connection", e);
            return courses;
        }

        try {
            statement = connection.prepareStatement(SELECT_BY_STUDENT_ID);
            statement.setInt(1, studentId);
            resultSet = statement.executeQuery();
            courses = processResultSet(resultSet);
        } catch (SQLException e) {
            logger.error("Cannot retrieve courses", e);
        } finally {
            ConnectionUtils.closeQuietly(resultSet);
            ConnectionUtils.closeQuietly(statement);
            ConnectionUtils.closeQuietly(connection);
        }

        return courses;
    }

    private void prepareInsertionCoursesStatements(List<Course> courses, PreparedStatement statementCourses,
            PreparedStatement statementStudents) throws SQLException {

        for (Course course : courses) {

            statementCourses.setInt(1, course.getId());
            statementCourses.setString(2, course.getName());
            statementCourses.setString(3, course.getDescription());
            statementCourses.addBatch();

            for (Student student : course.getStudents()) {
                statementStudents.setInt(1, course.getId());
                statementStudents.setInt(2, student.getId());
                statementStudents.addBatch();
            }
        }

    }

    private List<Course> processResultSet(ResultSet resultSet) throws SQLException {

        List<Course> courses = new ArrayList<>();

        while (resultSet.next()) {
            Course course = new Course(resultSet.getInt("course_id"), resultSet.getString("course_name"),
                    resultSet.getString("course_description"));
            courses.add(course);
        }

        return courses;

    }

}
