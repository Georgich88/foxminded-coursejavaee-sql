package com.foxminded.rodin.courses.cli;

import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.foxminded.rodin.courses.dao.CourseDao;
import com.foxminded.rodin.courses.dao.GroupDao;
import com.foxminded.rodin.courses.dao.StudentDao;
import com.foxminded.rodin.courses.dao.jdbc.CourseDaoJdbc;
import com.foxminded.rodin.courses.dao.jdbc.GroupDaoJdbc;
import com.foxminded.rodin.courses.dao.jdbc.StudentDaoJdbc;
import com.foxminded.rodin.courses.domain.Group;
import com.foxminded.rodin.courses.domain.Student;

public class CommandLineInterface {

    private static final String CLI_TEXT_MAIN_MENU = "MENU\n"
            + "a. Find all groups with less or equals student count\n"
            + "b. Find all students related to course with given name\n"
            + "c. Add new student\n"
            + "d. Delete student by STUDENT_ID\n"
            + "e. Add a student to the course (from a list)\n"
            + "f. Remove the student from one of his or her courses\n"
            + "q. Quit\n" + "Enter menu-letter $ ";
    private static final String CLI_WARNING_WRONG_CHOICE = "Enter a letter from 'a' to 'g'!";
    private static final String CLI_TEXT_FIND_GROUPS = "Find all groups with less or equals student count. Enter students count: ";
    private static final String CLI_TEXT_FIND_STUDENTS_BY_COURSENAME = "Find students by course name. Enter course name: ";
    private static final String CLI_TEXT_ADD_NEW_STUDENT = "Add a new student:  \n";
    private static final String CLI_TEXT_INPUT_FIRST_STUDENTNAME = "Enter first name: ";
    private static final String CLI_TEXT_INPUT_LAST_STUDENTNAME = "Enter last name: ";
    private static final String CLI_TEXT_DELETE_STUDENT_BY_ID = "Delete student by STUDENT_ID: ";
    private static final String CLI_TEXT_ADD_STUDENT_TO_COURSE = "Add a student to the course: ";
    private static final String CLI_TEXT_FIND_STUDENTS = "List of students: ";
    private static final String CLI_TEXT_FIND_COURSES = "List of courses: ";
    private static final String CLI_TEXT_INPUT_STUDENT_ID = "Enter student ID: ";
    private static final String CLI_TEXT_INPUT_COURSE_ID = "Enter course ID: ";
    private static final String CLI_WARNING_WRONG_ID = "Error, wrong IDs entered!";
    private static final String CLI_WARNING_WRONG_NUMBER = "Error, please enter number!";
    private static final String CLI_TEXT_DELETE_STUDENT_FROM_COURSE = "Remove the student from a course";

    private Scanner scanner;
    private CourseDao courseDao;
    private GroupDao groupDao;
    private StudentDao studentDao;

    public CommandLineInterface(Scanner scanner) {

        this.scanner = scanner;
        this.courseDao = new CourseDaoJdbc();
        this.groupDao = new GroupDaoJdbc();
        this.studentDao = new StudentDaoJdbc();
    }

    public void runInterface() {

        boolean isExit = false;

        printMainMenu();

        while (!isExit) {
            switch (scanner.next()) {
            case "a":
                findGroups();
                break;
            case "b":
                findStudentsByCourseName();
                break;
            case "c":
                addNewStudent();
                break;
            case "d":
                deleteStudentById();
                break;
            case "e":
                addStudentToCourse();
                break;
            case "f":
                removeStudentCourse();
                break;
            case "q":
                isExit = true;
                break;
            default:
                System.out.println(CLI_WARNING_WRONG_CHOICE);
                break;
            }
        }

        scanner.close();

    }

    private void printMainMenu() {
        System.out.println(CLI_TEXT_MAIN_MENU);
    }

    private void findGroups() {
        System.out.println(CLI_TEXT_FIND_GROUPS);
        List<Group> groups = groupDao.findByStudentsCountsLessEqual(getNumber());
        groups.forEach(System.out::println);
    }

    private void findStudentsByCourseName() {
        System.out.println(CLI_TEXT_FIND_STUDENTS_BY_COURSENAME);
        String courseName = scanner.next();
        System.out.println(studentDao.findByCourseName(courseName));
    }

    private void addNewStudent() {

        System.out.println(CLI_TEXT_ADD_NEW_STUDENT);
        System.out.println(CLI_TEXT_INPUT_FIRST_STUDENTNAME);
        String firstName = scanner.next();

        System.out.println(CLI_TEXT_INPUT_LAST_STUDENTNAME);
        String lastName = scanner.next();

        Student newStudent = new Student(0, firstName, lastName);
        studentDao.save(newStudent);
    }

    private void deleteStudentById() {
        System.out.println(CLI_TEXT_DELETE_STUDENT_BY_ID);
        studentDao.deleteById(getNumber());
    }

    private void addStudentToCourse() {

        System.out.println(CLI_TEXT_ADD_STUDENT_TO_COURSE);
        System.out.println(CLI_TEXT_FIND_STUDENTS);
        studentDao.findAll().forEach(System.out::println);
        ;

        System.out.println(CLI_TEXT_INPUT_STUDENT_ID);
        int studentId = getNumber();

        System.out.println(CLI_TEXT_FIND_COURSES);
        courseDao.findAll().forEach(System.out::println);

        System.out.println(CLI_TEXT_INPUT_COURSE_ID);
        int courseId = getNumber();

        if (studentId > 0 && courseId > 0) {
            studentDao.assignToCourse(studentId, courseId);
        } else {
            System.out.println(CLI_WARNING_WRONG_ID);
        }
    }

    private void removeStudentCourse() {

        System.out.println(CLI_TEXT_DELETE_STUDENT_FROM_COURSE);
        System.out.println(CLI_TEXT_FIND_STUDENTS);
        studentDao.findAll().forEach(System.out::println);

        System.out.println(CLI_TEXT_INPUT_STUDENT_ID);
        int studentId = getNumber();

        System.out.println(CLI_TEXT_FIND_COURSES);
        courseDao.findByStudentId(studentId).forEach(System.out::println);

        System.out.println(CLI_TEXT_INPUT_COURSE_ID);
        int courseId = getNumber();

        if (studentId > 0 && courseId > 0) {
            studentDao.deleteFromCourse(studentId, courseId);
        } else {
            System.out.println(CLI_WARNING_WRONG_ID);
        }

    }

    private int getNumber() {
        int number = 0;
        while (number == 0) {
            try {
                number = scanner.nextInt();
            } catch (Exception e) {
                System.out.println(CLI_WARNING_WRONG_NUMBER);
            }
        }
        return number;
    }

    // cli common

    public static void main(String[] args) {

        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ant", options);


        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("block-size")) {
                System.out.println(line.getOptionValue("block-size"));
            }
        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
    }

    private static Options createOptions() {

        Options options = new Options();

        Option findAllGroupsLEStudentCount = Option.builder("a")
                .desc("find all groups with less or equals student count")
                .hasArg()
                .argName("COUNT")
                .build();

        Option findAllStudentsWithCourseGivenName = Option.builder("b")
                .desc("find all students related to course with given name")
                .hasArg()
                .argName("COURSE_NAME")
                .build();

        Option addNewStudent = Option.builder("c")
                .desc("add new student")
                .hasArg()
                .numberOfArgs(2)
                .longOpt("new-student")
                .argName("FIRST_NAME> <LAST_NAME")
                .build();

        Option deleteStudentById = Option.builder("d")
                .desc("delete student")
                .hasArg()
                .longOpt("delete-student")
                .argName("STUDENT_ID")
                .build();

        Option assignStudentToCourse = Option.builder("e")
                .desc("add a student to the course")
                .hasArg()
                .numberOfArgs(2)
                .longOpt("assign-to-course")
                .argName("STUDENT_ID> <COURSE_ID")
                .build();

        Option expelStudentFromCourse = Option.builder("f")
                .desc("remove the student from one of his or her courses")
                .hasArg()
                .numberOfArgs(2)
                .longOpt("remove-from-course")
                .argName("STUDENT_ID> <COURSE_ID")
                .build();

        Option quitFromCLI = Option.builder("q")
                .desc("Quit")
                .build();

        options.addOption(findAllGroupsLEStudentCount);
        options.addOption(findAllStudentsWithCourseGivenName);
        options.addOption(addNewStudent);
        options.addOption(deleteStudentById);
        options.addOption(assignStudentToCourse);
        options.addOption(expelStudentFromCourse);
        options.addOption(quitFromCLI);

        return options;
    }

}
