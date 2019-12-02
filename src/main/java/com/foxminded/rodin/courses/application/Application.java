package com.foxminded.rodin.courses.application;

import java.util.List;

import com.foxminded.rodin.courses.cli.CommandLineInterface;
import com.foxminded.rodin.courses.dao.CourseDao;
import com.foxminded.rodin.courses.dao.GroupDao;
import com.foxminded.rodin.courses.dao.QueryExecutor;
import com.foxminded.rodin.courses.dao.StudentDao;
import com.foxminded.rodin.courses.dao.jdbc.CourseDaoJdbc;
import com.foxminded.rodin.courses.dao.jdbc.GroupDaoJdbc;
import com.foxminded.rodin.courses.dao.jdbc.StudentDaoJdbc;
import com.foxminded.rodin.courses.domain.Course;
import com.foxminded.rodin.courses.domain.Group;
import com.foxminded.rodin.courses.domain.Student;

public class Application {

    public static void main(String[] args) {

        initiateDatabase();
        generateData();
        runCommandUserInterface();
        deleteDatabase();
    }

    public static void runCommandUserInterface() {
        CommandLineInterface userInterface = new CommandLineInterface();
        userInterface.runCommandLineInterface();
    }

    public static void initiateDatabase() {

        QueryExecutor.createUser();
        QueryExecutor.createDatabase();
        QueryExecutor.createTables();

    }

    public static void deleteDatabase() {
        QueryExecutor.deleteDatabase();
    }

    public static void generateData() {

        DataGenerator dataGenerator = new DataGenerator();
        List<Group> groups = dataGenerator.generateGroups(10);
        List<Course> courses = dataGenerator.generateCourses(10);
        List<Student> students = dataGenerator.generateStudents(200);

        dataGenerator.assignStudentsToGroups(groups, students, 10, 30);
        dataGenerator.assignCoursesToStudents(courses, students, 1, 3);

        GroupDao groupDao = new GroupDaoJdbc();
        groupDao.saveAll(groups);

        StudentDao studentDao = new StudentDaoJdbc();
        studentDao.saveAll(students);

        CourseDao courseDao = new CourseDaoJdbc();
        courseDao.saveAll(courses);
    }

}
