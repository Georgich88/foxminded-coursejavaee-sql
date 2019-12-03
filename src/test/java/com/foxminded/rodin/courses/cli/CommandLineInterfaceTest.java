package com.foxminded.rodin.courses.cli;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.foxminded.rodin.courses.commandlineinterface.CommandLineInterface;
import com.foxminded.rodin.courses.dao.CourseDao;
import com.foxminded.rodin.courses.dao.QueryExecutor;
import com.foxminded.rodin.courses.dao.StudentDao;
import com.foxminded.rodin.courses.dao.jdbc.CourseDaoJdbc;
import com.foxminded.rodin.courses.dao.jdbc.StudentDaoJdbc;
import com.foxminded.rodin.courses.domain.Course;
import com.foxminded.rodin.courses.domain.Student;

public class CommandLineInterfaceTest {

    @BeforeEach
    public void initiateDatabaseBeforeTests() {
        QueryExecutor.createUser();
        QueryExecutor.createDatabase();
        QueryExecutor.createTables();
    }

    @Test
    public void shouldQuit() {

        CommandLineInterface userInterface = new CommandLineInterface();
        assertTrue(userInterface.executeCommand(new String[] { "-q" }));
    }

    @Test
    public void shouldAddNewStudent() {

        CommandLineInterface userInterface = new CommandLineInterface();
        userInterface.executeCommand(new String[] { "-n John, Jones" });

        StudentDao studentDao = new StudentDaoJdbc();

        List<Student> savedStudents = studentDao.findAll();

        String expectedResult = "[Student [id=0, group_id=0, firstName= John, lastName= Jones]]";

        assertEquals(expectedResult, savedStudents.toString());
    }

    @Test
    public void shouldDeleteStudentById() {

        List<Student> students = new ArrayList<Student>(4);

        students.add(new Student(0, "John", "Jones"));
        students.add(new Student(1, "Israel", "Adesanya"));
        students.add(new Student(2, "Stipe", "Miocic"));

        StudentDao studentDao = new StudentDaoJdbc();

        studentDao.saveAll(students);

        CommandLineInterface userInterface = new CommandLineInterface();
        userInterface.executeCommand(new String[] { "-d 1" });

        List<Student> savedStudents = studentDao.findAll();
        savedStudents.sort((Student student1, Student student2) -> {
            return student1.getId() - student2.getId();
        });

        String expectedResult = "[Student [id=0, group_id=0, firstName=John, lastName=Jones], Student [id=2, group_id=0, firstName=Stipe, lastName=Miocic]]";

        assertEquals(expectedResult, savedStudents.toString());
    }

    @Test
    public void shouldAssignToCourses() {

        CommandLineInterface userInterface = new CommandLineInterface();
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

        userInterface.executeCommand(new String[] { "-a 1, 1" });
        userInterface.executeCommand(new String[] { "-a 1, 2" });
        userInterface.executeCommand(new String[] { "-a 3, 2" });
        userInterface.executeCommand(new String[] { "-a 4, 2" });

        assertEquals(2, courseDao.findByStudentId(1).size());
        assertEquals(1, courseDao.findByStudentId(3).size());
        assertEquals(1, courseDao.findByStudentId(4).size());
    }

}
