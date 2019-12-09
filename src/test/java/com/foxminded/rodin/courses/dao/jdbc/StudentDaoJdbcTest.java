package com.foxminded.rodin.courses.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.foxminded.rodin.courses.dao.CourseDao;
import com.foxminded.rodin.courses.dao.QueryExecutor;
import com.foxminded.rodin.courses.dao.StudentDao;
import com.foxminded.rodin.courses.domain.Course;
import com.foxminded.rodin.courses.domain.Student;

public class StudentDaoJdbcTest {

    @BeforeEach
    public void initiateDatabaseBeforeTests() {
        QueryExecutor.createUser();
        QueryExecutor.createDatabase();
        QueryExecutor.createTables();
    }

    @Test
    public void shouldSaveStudents() {

        List<Student> students = new ArrayList<Student>(4);

        students.add(new Student(1, "John", "Jones"));
        students.add(new Student(2, "Israel", "Adesanya"));
        students.add(new Student(3, "Stipe", "Miocic"));
        students.add(new Student(4, "Kamaru", "Usman"));

        StudentDao studentDao = new StudentDaoJdbc();

        studentDao.saveAll(students);

        List<Student> savedStudents = studentDao.findAll();

        String expectedResult = "[Student [id=1, group_id=0, firstName=John, lastName=Jones], Student [id=2, group_id=0, firstName=Israel, lastName=Adesanya], Student [id=3, group_id=0, firstName=Stipe, lastName=Miocic], Student [id=4, group_id=0, firstName=Kamaru, lastName=Usman]]";

        assertEquals(expectedResult, savedStudents.toString());
    }

    @Test
    public void shouldDeleteStudentById() {

        List<Student> students = new ArrayList<Student>(3);

        students.add(new Student(1, "John", "Jones"));
        students.add(new Student(2, "Israel", "Adesanya"));
        students.add(new Student(3, "Stipe", "Miocic"));

        StudentDao studentDao = new StudentDaoJdbc();

        studentDao.save(students.get(0));
        studentDao.save(students.get(1));
        studentDao.save(students.get(2));

        studentDao.deleteById(2);

        List<Student> savedStudents = studentDao.findAll();

        String expectedResult = "[Student [id=1, group_id=0, firstName=John, lastName=Jones], Student [id=3, group_id=0, firstName=Stipe, lastName=Miocic]]";

        assertEquals(expectedResult, savedStudents.toString());

    }

    @Test
    public void shouldAssignToCourses() {

        StudentDao studentDao = new StudentDaoJdbc();
        CourseDao courseDao = new CourseDaoJdbc();

        List<Course> courses = new ArrayList<Course>(4);
        courses.add(new Course(0, "Math", "Mathematics"));
        courses.add(new Course(1, "Biology", "Anatomy, biophysics, cell and molecular biology, computational biology"));

        List<Student> students = new ArrayList<Student>(4);

        students.add(new Student(0, "John", "Jones"));
        students.add(new Student(1, "Israel", "Adesanya"));
        students.add(new Student(2, "Stipe", "Miocic"));
        students.add(new Student(3, "Kamaru", "Usman"));

        studentDao.saveAll(students);
        courseDao.saveAll(courses);

        studentDao.assignToCourse(0, 0);
        studentDao.assignToCourse(0, 1);
        studentDao.assignToCourse(2, 1);
        studentDao.assignToCourse(3, 1);

        assertEquals(2, courseDao.findByStudentId(0).size());
        assertEquals(1, courseDao.findByStudentId(2).size());
        assertEquals(1, courseDao.findByStudentId(3).size());
    }

    @Test
    public void shouldFindByCourseName() {

        StudentDao studentDao = new StudentDaoJdbc();
        CourseDao courseDao = new CourseDaoJdbc();

        List<Course> courses = new ArrayList<Course>(4);
        courses.add(new Course(1, "Math", "Mathematics"));
        courses.add(new Course(2, "Biology", "Anatomy, biophysics, cell and molecular biology, computational biology"));

        List<Student> students = new ArrayList<Student>(4);

        students.add(new Student(1, "John", "Jones"));
        students.add(new Student(2, "Israel", "Adesanya"));
        students.add(new Student(3, "Stipe", "Miocic"));
        students.add(new Student(4, "Kamaru", "Usman"));

        studentDao.saveAll(students);
        courseDao.saveAll(courses);

        studentDao.assignToCourse(1, 1);
        studentDao.assignToCourse(1, 2);
        studentDao.assignToCourse(3, 2);
        studentDao.assignToCourse(4, 2);

        assertEquals(
                "[Student [id=1, group_id=0, firstName=John, lastName=Jones], "
                        + "Student [id=3, group_id=0, firstName=Stipe, lastName=Miocic], "
                        + "Student [id=4, group_id=0, firstName=Kamaru, lastName=Usman]]",
                studentDao.findByCourseName("Biology").toString());

    }

    @Test
    public void shouldDeleteFromCourses() {

        StudentDao studentDao = new StudentDaoJdbc();
        CourseDao courseDao = new CourseDaoJdbc();

        List<Course> courses = new ArrayList<Course>(4);
        courses.add(new Course(0, "Math", "Mathematics"));
        courses.add(new Course(1, "Biology", "Anatomy, biophysics, cell and molecular biology, computational biology"));

        List<Student> students = new ArrayList<Student>(4);

        students.add(new Student(0, "John", "Jones"));
        students.add(new Student(1, "Israel", "Adesanya"));
        students.add(new Student(2, "Stipe", "Miocic"));
        students.add(new Student(3, "Kamaru", "Usman"));

        studentDao.saveAll(students);
        courseDao.saveAll(courses);

        studentDao.assignToCourse(0, 0);
        studentDao.assignToCourse(0, 1);
        studentDao.assignToCourse(2, 1);
        studentDao.assignToCourse(3, 1);

        studentDao.deleteFromCourse(0, 0);

        assertEquals(1, courseDao.findByStudentId(0).size());
        assertEquals(1, courseDao.findByStudentId(2).size());
        assertEquals(1, courseDao.findByStudentId(3).size());

    }

}
