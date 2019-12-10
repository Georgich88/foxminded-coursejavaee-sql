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

    private static final String INSERTION_QUERY_TEMPLATE = "INSERT INTO courses (course_id, course_name, course_description) VALUES (?, ?, ?)";
    private static final int INSERTION_COURSE_ID_PARAMETER = 1;
    private static final int INSERTION_COURSE_NAME_PARAMETER = 2;
    private static final int INSERTION_COURSE_DESCRIPTION_PARAMETER = 3;

    private static final String COURSE_STUDENTS_INSERTION_QUERY_TEMPLATE = "INSERT INTO courses_students (course_id, student_id) VALUES (?, ?)";
    private static final int COURSE_STUDENTS_INSERTION_COURSE_ID_PARAMETER = 1;
    private static final int COURSE_STUDENTS_INSERTION_STUDENT_ID_PARAMETER = 2;

    private static final String SELECTION_ALL_COURSES_QUERY_TEMPLATE = "SELECT course_id, course_name, course_description FROM public.courses";
    private static final String SELECTION_BY_STUDENT_ID_QUERY_TEMPLATE =
            "SELECT courses.course_id, courses.course_name, courses.course_description " + 
            "FROM courses_students " + 
            "INNER JOIN courses " + 
            "ON courses_students.course_id = courses.course_id " + 
            "WHERE courses_students.student_id = ?";
    private static final String SELECTION_COURSE_ID = "course_id";
    private static final String SELECTION_COURSE_NAME = "course_name";
    private static final String SELECTION_COURSE_DESCRIPTION = "course_description";

    private final static Logger logger = Logger.getLogger(CourseDaoJdbc.class);
    
    @Override
    public void saveAll(List<Course> courses) {

        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statementCourses = connection.prepareStatement(INSERTION_QUERY_TEMPLATE);
                PreparedStatement statementStudents = connection
                        .prepareStatement(COURSE_STUDENTS_INSERTION_QUERY_TEMPLATE)) {

            prepareInsertionCoursesStatements(courses, statementCourses, statementStudents);
            statementCourses.executeBatch();
            statementStudents.executeBatch();
            
        } catch (SQLException e) {
            logger.error("Cannot save courses", e);
        } 
        
    }

    @Override
    public List<Course> findAll() {

        List<Course> courses = new ArrayList<Course>();

        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECTION_ALL_COURSES_QUERY_TEMPLATE);
                ResultSet resultSet = statement.executeQuery()) {
            courses = processResultSet(resultSet);
        } catch (SQLException e) {
            logger.error("Cannot save courses", e);
        } 

        return courses;
    }

    @Override
    public List<Course> findByStudentId(int studentId) {

        List<Course> courses = new ArrayList<Course>();

        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECTION_BY_STUDENT_ID_QUERY_TEMPLATE)) {
            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();
            courses = processResultSet(resultSet);
        } catch (SQLException e) {
            logger.error("Cannot retrieve courses", e);
        }

        return courses;
    }

    private void prepareInsertionCoursesStatements(List<Course> courses, PreparedStatement statementCourses,
            PreparedStatement statementStudents) throws SQLException {

        for (Course course : courses) {

            statementCourses.setInt(INSERTION_COURSE_ID_PARAMETER, course.getId());
            statementCourses.setString(INSERTION_COURSE_NAME_PARAMETER, course.getName());
            statementCourses.setString(INSERTION_COURSE_DESCRIPTION_PARAMETER, course.getDescription());
            statementCourses.addBatch();

            for (Student student : course.getStudents()) {

                statementStudents.setInt(COURSE_STUDENTS_INSERTION_COURSE_ID_PARAMETER, course.getId());
                statementStudents.setInt(COURSE_STUDENTS_INSERTION_STUDENT_ID_PARAMETER, student.getId());
                statementStudents.addBatch();
            }
        }

    }

    private List<Course> processResultSet(ResultSet resultSet) throws SQLException {

        List<Course> courses = new ArrayList<>();

        while (resultSet.next()) {
            Course course = new Course(resultSet.getInt(SELECTION_COURSE_ID),
                    resultSet.getString(SELECTION_COURSE_NAME), 
                    resultSet.getString(SELECTION_COURSE_DESCRIPTION));
            courses.add(course);
        }

        return courses;

    }

}
