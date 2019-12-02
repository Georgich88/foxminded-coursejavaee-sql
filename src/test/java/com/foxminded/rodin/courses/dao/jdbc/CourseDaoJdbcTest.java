package com.foxminded.rodin.courses.dao.jdbc;

import static org.junit.Assert.assertTrue;
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

public class CourseDaoJdbcTest {

    @BeforeEach
    public void initiateDatabaseBeforeTests() {
        QueryExecutor.createUser();
        QueryExecutor.createDatabase();
        QueryExecutor.createTables();
    }

    @Test
    public void shouldSaveCourses() {

        CourseDao courseDao = new CourseDaoJdbc();

        List<Course> courses = new ArrayList<Course>(4);
        courses.add(new Course(0, "Math", "Mathematics"));
        courses.add(new Course(1, "Biology", "Anatomy, biophysics, cell and molecular biology, computational biology"));
        courses.add(new Course(2, "Literature", "Main subjects of courses in literature include: cultural and literature, "
                        + "topics in literary research, reading fiction, poetry, modern drama, classical literature, women's literature. "));
        courses.add(new Course(3, "Physics",
                "Challenge because many a concept in Physics are challenging and can strain your cognitive tissues"));


        courseDao.saveAll(courses);

        List<Course> savedCourses = courseDao.findAll();

        String expectedResult = "[Course [id=0, name=Math, description=Mathematics, students=[]], "
                + "Course [id=1, name=Biology, description=Anatomy, biophysics, cell and molecular biology, computational biology, students=[]], "
                + "Course [id=2, name=Literature, description=Main subjects of courses in literature include: cultural and literature, topics in literary research, reading fiction, poetry, modern drama, classical literature, women's literature. , students=[]], "
                + "Course [id=3, name=Physics, description=Challenge because many a concept in Physics are challenging and can strain your cognitive tissues, students=[]]]";

        assertEquals(expectedResult, savedCourses.toString());
    }

    @Test
    public void findByStudentId() {

        CourseDao courseDao = new CourseDaoJdbc();
        StudentDao studentDao = new StudentDaoJdbc();      
        
        List<Student> students = new ArrayList<Student>(1);
        Student student = new Student(0, "John", "Jones");
        List<Course> courses = new ArrayList<Course>(4);
        courses.add(new Course(0, "Math", "Mathematics"));
        courses.add(new Course(1, "Biology", "Anatomy, biophysics, cell and molecular biology, computational biology"));
        courses.add(new Course(2, "Literature", "Main subjects of courses in literature include: "
                + "cultural and literature, topics in literary research, reading fiction, poetry, modern drama, classical literature, women's literature. "));
        courses.add(new Course(3, "Physics",
                "Challenge because many a concept in Physics are challenging and can strain your cognitive tissues"));
       
        courses.get(0).assignStudent(student);
        courses.get(2).assignStudent(student);
        
        studentDao.saveAll(students);
        courseDao.saveAll(courses);

        List<Course> savedCourses = courseDao.findByStudentId(student.getId());

        assertTrue(savedCourses.size() == 2);
        assertTrue(savedCourses.stream()
                .filter(course -> {
                    return course.getId() == 0;
                })
                .count() == 1);
        assertTrue(savedCourses.stream()
                .filter(course -> {
                    return course.getId() == 2;
                })
                .count() == 1);
    }

}
