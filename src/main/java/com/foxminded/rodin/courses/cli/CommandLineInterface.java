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
import org.apache.log4j.Logger;

import com.foxminded.rodin.courses.dao.CourseDao;
import com.foxminded.rodin.courses.dao.GroupDao;
import com.foxminded.rodin.courses.dao.StudentDao;
import com.foxminded.rodin.courses.dao.jdbc.CourseDaoJdbc;
import com.foxminded.rodin.courses.dao.jdbc.GroupDaoJdbc;
import com.foxminded.rodin.courses.dao.jdbc.StudentDaoJdbc;
import com.foxminded.rodin.courses.domain.Group;
import com.foxminded.rodin.courses.domain.Student;

public class CommandLineInterface {

    private static final String ARG_STUDENTS_COUNT = "COUNT";
    private static final String ARG_STUDENTS_COURSE_NAME = "COURSE_NAME";
    private static final String ARG_STUDENT_FIRST_NAME = "FIRSTNAME";
    private static final String ARG_STUDENT_LAST_NAME = "LASTNAME";
    private static final String ARG_STUDENT_ID = "STUDENT_ID";
    private static final String ARG_COURSE_ID = "COURSE_ID";
    private static final char ARGS_SEPARATOR = ',';
    private static final String ARGS_TWO_VALUES_FORMAT = "%s%s %s";

    private static final String OPT_FIND_GROUPS = "g";
    private static final String OPT_FIND_GROUPS_LONG = "find-groups";
    private static final String OPT_FIND_GROUPS_DESC = "find all groups with less or equals student count";

    private static final String OPT_FIND_STUDENTS_BY_COURSENAME = "s";
    private static final String OPT_FIND_STUDENTS_BY_COURSENAME_LONG = "find-students";
    private static final String OPT_FIND_STUDENTS_BY_COURSENAME_DESC = "find all students related to course with given name";

    private static final String OPT_ADD_NEW_STUDENT = "n";
    private static final String OPT_ADD_NEW_STUDENT_LONG = "new-student";
    private static final String OPT_ADD_NEW_STUDENT_DESC = "add new student";

    private static final String OPT_DELETE_STUDENT_BY_ID = "d";
    private static final String OPT_DELETE_STUDENT_BY_ID_LONG = "delete-student";
    private static final String OPT_DELETE_STUDENT_BY_ID_DESC = "delete student by STUDENT_ID";

    private static final String OPT_ADD_STUDENT_TO_COURSE = "a";
    private static final String OPT_ADD_STUDENT_TO_COURSE_LONG = "assign-to-course";
    private static final String OPT_ADD_STUDENT_TO_COURSE_DESC = "add a student to the course";

    private static final String OPT_REMOVE_STUDENT_FROM_COURSE = "r";
    private static final String OPT_REMOVE_STUDENT_FROM_COURSE_LONG = "remove-from-course";
    private static final String OPT_REMOVE_STUDENT_FROM_COURSE_DESC = "remove the student from one of his or her courses";

    private static final String OPT_QUIT = "q";
    private static final String OPT_QUIT_LONG = "quit";
    private static final String OPT_QUIT_DESC = "exit from application";

    private static final String CLI_WARNING_WRONG_ID = "Error, wrong IDs entered!";

    private Scanner scanner;
    private Options options;
    private CourseDao courseDao;
    private GroupDao groupDao;
    private StudentDao studentDao;

    private final static Logger logger = Logger.getLogger(CommandLineInterface.class);

    public CommandLineInterface() {

        this.scanner = new Scanner(System.in);
        this.options = createOptions();
        this.courseDao = new CourseDaoJdbc();
        this.groupDao = new GroupDaoJdbc();
        this.studentDao = new StudentDaoJdbc();
    }


    public void runCommandLineInterface() {

        boolean isExit = false;

        printHelp();

        while (!isExit) {
            String arg = scanner.nextLine().trim();
            isExit = executeCommand(new String[] { arg });
        }

        scanner.close();

    }

    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("courses API", options);
    }

    private void findGroups(int count) {
        List<Group> groups = groupDao.findByStudentsCountsLessEqual(count);
        groups.forEach(System.out::println);
    }

    private void findStudentsByCourseName(String courseName) {
        System.out.println(studentDao.findByCourseName(courseName));
    }

    private void addNewStudent(String firstName, String lastName) {

        Student newStudent = new Student(0, firstName, lastName);
        studentDao.save(newStudent);
    }

    private void deleteStudentById(int studentId) {
        studentDao.deleteById(studentId);
    }

    private void addStudentToCourse(int studentId, int courseId) {

        if (studentId > 0 && courseId > 0) {
            studentDao.assignToCourse(studentId, courseId);
        } else {
            logger.warn(CLI_WARNING_WRONG_ID);
        }
    }

    private void removeStudentCourse(int studentId, int courseId) {

        if (studentId > 0 && courseId > 0) {
            studentDao.deleteFromCourse(studentId, courseId);
        } else {
            logger.warn(CLI_WARNING_WRONG_ID);
        }

    }

    public boolean executeCommand(String[] args) {

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption(OPT_QUIT)) {
                return true;
            } else if (line.hasOption(OPT_FIND_GROUPS)) {
                findGroups(parseSingleIntValue(line, OPT_FIND_GROUPS));
            } else if (line.hasOption(OPT_FIND_STUDENTS_BY_COURSENAME)) {
                String courseName = parseSingleStringValue(line, OPT_FIND_STUDENTS_BY_COURSENAME);
                findStudentsByCourseName(courseName);
            } else if (line.hasOption(OPT_ADD_NEW_STUDENT)) {
                var studentName = line.getOptionValues(OPT_ADD_NEW_STUDENT);
                addNewStudent(studentName[0], studentName[1]);
            } else if (line.hasOption(OPT_DELETE_STUDENT_BY_ID)) {
                deleteStudentById(parseSingleIntValue(line, OPT_DELETE_STUDENT_BY_ID));
            } else if (line.hasOption(OPT_ADD_STUDENT_TO_COURSE)) {
                var ids = parseIntValues(line, OPT_ADD_STUDENT_TO_COURSE);
                addStudentToCourse(ids[0], ids[1]);
            } else if (line.hasOption(OPT_REMOVE_STUDENT_FROM_COURSE)) {
                var ids = parseIntValues(line, OPT_ADD_STUDENT_TO_COURSE);
                removeStudentCourse(ids[0], ids[1]);
            }
        } catch (ParseException exp) {
            logger.error("Parsing failed.", exp);
        }

        return false;
    }

    private static int parseSingleIntValue(CommandLine line, String optionName)
            throws NumberFormatException, ParseException {
        return Integer.parseInt(line.getParsedOptionValue(optionName).toString().trim());
    }

    private static int[] parseIntValues(CommandLine line, String optionName)
            throws NumberFormatException, ParseException {
        
        var stringValues = line.getOptionValues(OPT_ADD_STUDENT_TO_COURSE);
        int[] intValues = new int[stringValues.length];

        for (int i = 0; i < stringValues.length; i++) {
            intValues[i] = Integer.parseInt(stringValues[i].trim());
        }
       
        return intValues;
    }

    private static String parseSingleStringValue(CommandLine line, String optionName)
            throws NumberFormatException, ParseException {
        return line.getParsedOptionValue(optionName).toString().trim();
    }

    private static Options createOptions() {

        Options options = new Options();

        Option findAllGroupsLEStudentCount = Option.builder(OPT_FIND_GROUPS)
                .longOpt(OPT_FIND_GROUPS_LONG)
                .desc(OPT_FIND_GROUPS_DESC)
                .hasArg()
                .argName(ARG_STUDENTS_COUNT)
                .build();

        Option findAllStudentsWithCourseGivenName = Option.builder(OPT_FIND_STUDENTS_BY_COURSENAME)
                .longOpt(OPT_FIND_STUDENTS_BY_COURSENAME_LONG)
                .desc(OPT_FIND_STUDENTS_BY_COURSENAME_DESC)
                .hasArg()
                .argName(ARG_STUDENTS_COURSE_NAME)
                .build();

        Option addNewStudent = Option.builder(OPT_ADD_NEW_STUDENT)
                .longOpt(OPT_ADD_NEW_STUDENT_LONG)
                .desc(OPT_ADD_NEW_STUDENT_DESC)
                .hasArg()
                .numberOfArgs(2)
                .valueSeparator(ARGS_SEPARATOR)
                .argName(String.format(ARGS_TWO_VALUES_FORMAT, ARG_STUDENT_FIRST_NAME, ARGS_SEPARATOR,
                        ARG_STUDENT_LAST_NAME))
                .build();

        Option deleteStudentById = Option.builder(OPT_DELETE_STUDENT_BY_ID)
                .longOpt(OPT_DELETE_STUDENT_BY_ID_LONG)
                .desc(OPT_DELETE_STUDENT_BY_ID_DESC)
                .hasArg()
                .argName(ARG_STUDENT_FIRST_NAME)
                .build();

        Option assignStudentToCourse = Option.builder(OPT_ADD_STUDENT_TO_COURSE)
                .longOpt(OPT_ADD_STUDENT_TO_COURSE_LONG)
                .desc(OPT_ADD_STUDENT_TO_COURSE_DESC)
                .hasArg()
                .numberOfArgs(2)
                .valueSeparator(ARGS_SEPARATOR)
                .argName(String.format(ARGS_TWO_VALUES_FORMAT, ARG_STUDENT_ID, ARGS_SEPARATOR, ARG_COURSE_ID))
                .build();

        Option removeStudentFromCourse = Option.builder(OPT_REMOVE_STUDENT_FROM_COURSE)
                .longOpt(OPT_REMOVE_STUDENT_FROM_COURSE_LONG)
                .desc(OPT_REMOVE_STUDENT_FROM_COURSE_DESC)
                .hasArg()
                .numberOfArgs(2)
                .valueSeparator(ARGS_SEPARATOR)
                .argName(String.format(ARGS_TWO_VALUES_FORMAT, ARG_STUDENT_ID, ARGS_SEPARATOR, ARG_COURSE_ID))
                .build();

        Option quitFromCLI = Option.builder(OPT_QUIT)
                .longOpt(OPT_QUIT_LONG)
                .desc(OPT_QUIT_DESC)
                .build();

        options.addOption(findAllGroupsLEStudentCount);
        options.addOption(findAllStudentsWithCourseGivenName);
        options.addOption(addNewStudent);
        options.addOption(deleteStudentById);
        options.addOption(assignStudentToCourse);
        options.addOption(removeStudentFromCourse);
        options.addOption(quitFromCLI);

        return options;
    }

}
