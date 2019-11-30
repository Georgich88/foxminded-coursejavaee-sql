package com.foxminded.rodin.courses.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomStringUtils;

import com.foxminded.rodin.courses.domain.Course;
import com.foxminded.rodin.courses.domain.Group;
import com.foxminded.rodin.courses.domain.Student;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;

public class DataGenerator {

    private Faker randomDataGenerator;
    private Random randomNumberGenerator;

    public DataGenerator() {
        randomDataGenerator = new Faker();
        randomNumberGenerator = new Random();
    }

    /**
     * Creates new groups with randomly generated names.
     * 
     * @param number - groups number should be created.
     * @return list of groups.
     */
    public List<Group> generateGroups(int number) {

        List<Group> groups = new ArrayList<Group>(number);

        for (int i = 0; i < number; i++) {
            groups.add(createGroup(i + 1));
        }
        return groups;

    }

    /**
     * Creates new students with random first and last names.
     * 
     * @param number - students number should be created.
     * @return list of students.
     */
    public List<Student> generateStudents(int number) {

        List<Student> students = new ArrayList<Student>(number);

        for (int i = 0; i < number; i++) {
            students.add(createStudent(i + 1));
        }
        return students;
    }

    /**
     * Creates new random courses.
     * 
     * @param number - courses number should be created.
     * @return list of courses.
     */
    public List<Course> generateCourses(int number) {
        List<Course> courses = new ArrayList<Course>(number);

        for (int i = 0; i < number; i++) {
            courses.add(createCourse(i + 1));
        }
        return courses;
    }

    /**
     * Randomly assigns students to groups.
     * 
     * @param groups
     * @param students
     * @param minStudentsNumber
     * @param maxStudentsNumber
     */
    public void assignStudentsToGroups(List<Group> groups, List<Student> students, int minStudentsNumber,
            int maxStudentsNumber) {

        LinkedList<Student> studentsToAssign = new LinkedList<Student>(students);
        Collections.shuffle(studentsToAssign);

        groups.forEach(group -> {
            IntStream.rangeClosed(1, getRandomNumberInRange(minStudentsNumber, maxStudentsNumber))
                    .forEach(i -> {
                        if (!studentsToAssign.isEmpty()) {
                            studentsToAssign.removeFirst().setGroupId(group.getId());
                        }
            });

        });

    }

    /**
     * Randomly assigns students to courses.
     * 
     * @param courses          - a courses list to which students should be assigned
     * @param students         - course to which students should be assigned
     * @param minCoursesNumber
     * @param maxCoursesNumber
     */
    public void assignCoursesToStudents(List<Course> courses, List<Student> students, int minCoursesNumber,
            int maxCoursesNumber) {

        students.forEach(student -> {
            IntStream.rangeClosed(1, getRandomNumberInRange(minCoursesNumber, maxCoursesNumber)).forEach(i -> {
                assignStudentToRandomCourse(courses, student);
            });

        });

    }

    private Group createGroup(int id) {
        return new Group(id, generateRandomGroupName());
    }

    private Student createStudent(int id) {
        Name name = randomDataGenerator.name();
        return new Student(id, name.firstName(), name.lastName());
    }

    private Course createCourse(int id) {
        String name = randomDataGenerator.educator().course();
        return new Course(id, name, "Course of " + name);
    }

    private static String generateRandomGroupName() {
        return RandomStringUtils.randomAlphabetic(2) + "-" + RandomStringUtils.randomNumeric(2);
    }

    private int getRandomNumberInRange(int min, int max) {

        return randomNumberGenerator
                .ints(min, (max + 1))
                .limit(1).findFirst().getAsInt();

    }

    private void assignStudentToRandomCourse(List<Course> courses, Student student) {
        Course course = courses.get(getRandomNumberInRange(0, courses.size() - 1));
        course.assignStudent(student);
    }

}
