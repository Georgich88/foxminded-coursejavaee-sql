package com.foxminded.rodin.courses.application;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.foxminded.rodin.courses.domain.Course;
import com.foxminded.rodin.courses.domain.Group;
import com.foxminded.rodin.courses.domain.Student;

public class DataGeneratorTest {

    private DataGenerator dataGenerator;

    public DataGeneratorTest() {
        super();
        this.dataGenerator = new DataGenerator();
    }

    @Test
    public void shouldGenerateRandomGroups() {
        List<Group> groups = dataGenerator.generateGroups(10);
        assertEquals(10, groups.size());
    }

    @Test
    public void shouldGenerateRandomCourses() {
        List<Course> courses = dataGenerator.generateCourses(10);
        assertEquals(10, courses.size());
    }

    @Test
    public void shouldGenerateRandomStudents() {
        List<Student> students = dataGenerator.generateStudents(10);
        assertEquals(10, students.size());
    }

    @Test
    public void shouldAssignRandomStudentsToRandomGroups() {
        List<Student> students = new DataGenerator().generateStudents(100);
        List<Group> groups = new DataGenerator().generateGroups(100);
        dataGenerator.assignStudentsToGroups(groups, students, 12, 15);
        assertNotEquals(0, students.stream().filter(student -> {
            return (student.getGroupId() != 0);
        }).count());
    }

    @Test
    public void shouldAssignRandomStudentsToRandomCourses() {
        List<Student> students = new DataGenerator().generateStudents(10);
        List<Course> courses = new DataGenerator().generateCourses(10);
        dataGenerator.assignCoursesToStudents(courses, students, 1, 10);
    }

    @Test
    public void shouldExpelStudentFromCourse() {
        List<Student> students = new DataGenerator().generateStudents(100);
        List<Course> courses = new DataGenerator().generateCourses(1);
        dataGenerator.assignCoursesToStudents(courses, students, 100, 100);
        Course course = courses.get(0);
        Student student = students.get(0);
        assertTrue(course.expelStudent(student));
        assertEquals(students.size() - 1, course.getStudents().size());
    }

    @Test
    public void shouldExpelAllStudentsFromCourse() {
        List<Student> students = new DataGenerator().generateStudents(100);
        List<Course> courses = new DataGenerator().generateCourses(1);
        dataGenerator.assignCoursesToStudents(courses, students, 100, 100);
        Course course = courses.get(0);
        assertTrue(course.expelStudents(students));
        assertEquals(0, course.getStudents().size());
    }

}
