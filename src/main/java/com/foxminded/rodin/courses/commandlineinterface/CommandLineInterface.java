package com.foxminded.rodin.courses.commandlineinterface;

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

import com.foxminded.rodin.courses.dao.GroupDao;
import com.foxminded.rodin.courses.dao.StudentDao;
import com.foxminded.rodin.courses.dao.jdbc.GroupDaoJdbc;
import com.foxminded.rodin.courses.dao.jdbc.StudentDaoJdbc;
import com.foxminded.rodin.courses.domain.Group;
import com.foxminded.rodin.courses.domain.Student;

public class CommandLineInterface {

    private static final String ARG_STUDENTS_NUMBER_NAME = "COUNT";
    private static final String ARG_STUDENTS_COURSE_NAME = "COURSE_NAME";
    private static final String ARG_STUDENT_FIRST_NAME = "FIRSTNAME";
    private static final String ARG_STUDENT_LAST_NAME = "LASTNAME";
    private static final String ARG_STUDENT_ID = "STUDENT_ID";
    private static final String ARG_COURSE_ID = "COURSE_ID";
    private static final char ARGS_SEPARATOR = ',';
    private static final String ARGS_TWO_VALUES_FORMAT = "%s%s %s";

    private static final String OPT_FIND_GROUPS_SHORT = "g";
    private static final String OPT_FIND_GROUPS_LONG = "find-groups";
    private static final String OPT_FIND_GROUPS_DESC = "find all groups with less or equals student count";

    private static final String OPT_FIND_STUDENTS_BY_COURSE_NAME_SHORT = "s";
    private static final String OPT_FIND_STUDENTS_BY_COURSE_NAME_LONG = "find-students";
    private static final String OPT_FIND_STUDENTS_BY_COURSE_NAME_DESC = "find all students related to course with given name";

    private static final String OPT_ADD_NEW_STUDENT_SHORT = "n";
    private static final String OPT_ADD_NEW_STUDENT_LONG = "new-student";
    private static final String OPT_ADD_NEW_STUDENT_DESC = "add new student";

    private static final String OPT_DELETE_STUDENT_BY_ID_SHORT = "d";
    private static final String OPT_DELETE_STUDENT_BY_ID_LONG = "delete-student";
    private static final String OPT_DELETE_STUDENT_BY_ID_DESC = "delete student by STUDENT_ID";

    private static final String OPT_ADD_STUDENT_TO_COURSE_SHORT = "a";
    private static final String OPT_ADD_STUDENT_TO_COURSE_LONG = "assign-to-course";
    private static final String OPT_ADD_STUDENT_TO_COURSE_DESC = "add a student to the course";

    private static final String OPT_REMOVE_STUDENT_FROM_COURSE_SHORT = "r";
    private static final String OPT_REMOVE_STUDENT_FROM_COURSE_LONG = "remove-from-course";
    private static final String OPT_REMOVE_STUDENT_FROM_COURSE_DESC = "remove the student from one of his or her courses";

    private static final String OPT_QUIT = "q";
    private static final String OPT_QUIT_LONG = "quit";
    private static final String OPT_QUIT_DESC = "exit from application";

    private static final String CLI_WARNING_WRONG_ID = "Error, wrong IDs entered!";

    private static final Option OPT_FIND_GROUPS = Option.builder(OPT_FIND_GROUPS_SHORT)
            .longOpt(OPT_FIND_GROUPS_LONG)
            .desc(OPT_FIND_GROUPS_DESC)
            .hasArg()
            .argName(ARG_STUDENTS_NUMBER_NAME)
            .build();

    private static final Option OPT_FIND_STUDENTS_BY_COURSE_NAME = Option
            .builder(OPT_FIND_STUDENTS_BY_COURSE_NAME_SHORT)
            .longOpt(OPT_FIND_STUDENTS_BY_COURSE_NAME_LONG)
            .desc(OPT_FIND_STUDENTS_BY_COURSE_NAME_DESC)
            .hasArg()
            .argName(ARG_STUDENTS_COURSE_NAME)
            .build();

    private static final Option OPT_ADD_NEW_STUDENT = Option.builder(OPT_ADD_NEW_STUDENT_SHORT)
            .longOpt(OPT_ADD_NEW_STUDENT_LONG)
            .desc(OPT_ADD_NEW_STUDENT_DESC)
            .hasArg()
            .numberOfArgs(2)
            .valueSeparator(ARGS_SEPARATOR)
            .argName(String.format(ARGS_TWO_VALUES_FORMAT, ARG_STUDENT_FIRST_NAME, ARGS_SEPARATOR,
                    ARG_STUDENT_LAST_NAME))
            .build();

    private static final Option OPT_DELETE_STUDENT_BY_ID = Option.builder(OPT_DELETE_STUDENT_BY_ID_SHORT)
            .longOpt(OPT_DELETE_STUDENT_BY_ID_LONG)
            .desc(OPT_DELETE_STUDENT_BY_ID_DESC)
            .hasArg()
            .argName(ARG_STUDENT_FIRST_NAME)
            .build();

    private static final Option OPT_ADD_STUDENT_TO_COURSE = Option.builder(OPT_ADD_STUDENT_TO_COURSE_SHORT)
            .longOpt(OPT_ADD_STUDENT_TO_COURSE_LONG)
            .desc(OPT_ADD_STUDENT_TO_COURSE_DESC)
            .hasArg()
            .numberOfArgs(2)
            .valueSeparator(ARGS_SEPARATOR)
            .argName(String.format(ARGS_TWO_VALUES_FORMAT, ARG_STUDENT_ID, ARGS_SEPARATOR, ARG_COURSE_ID))
            .build();

    private static final Option OPT_REMOVE_STUDENT_FROM_COURSE = Option.builder(OPT_REMOVE_STUDENT_FROM_COURSE_SHORT)
            .longOpt(OPT_REMOVE_STUDENT_FROM_COURSE_LONG)
            .desc(OPT_REMOVE_STUDENT_FROM_COURSE_DESC)
            .hasArg()
            .numberOfArgs(2)
            .valueSeparator(ARGS_SEPARATOR)
            .argName(String.format(ARGS_TWO_VALUES_FORMAT, ARG_STUDENT_ID, ARGS_SEPARATOR, ARG_COURSE_ID))
            .build();

    private static final Option quitFromCLI = Option.builder(OPT_QUIT)
            .longOpt(OPT_QUIT_LONG)
            .desc(OPT_QUIT_DESC)
            .build();

    private Scanner scanner;
    private Options options;
    private GroupDao groupDao;
    private StudentDao studentDao;

    private final static Logger logger = Logger.getLogger(CommandLineInterface.class);

    public CommandLineInterface() {

        this.scanner = new Scanner(System.in);
        this.options = createOptions();
        this.groupDao = new GroupDaoJdbc();
        this.studentDao = new StudentDaoJdbc();
    }


    public void runCommandLineInterface() {

        boolean isExit = false;

        printHelp();

        do {
            String arg = scanner.nextLine().trim();
            isExit = executeCommand(new String[] { arg });
        } while (!isExit);

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
            } else if (line.hasOption(OPT_FIND_GROUPS_SHORT)) {
                findGroups(parseSingleIntValue(line, OPT_FIND_GROUPS_SHORT));
            } else if (line.hasOption(OPT_FIND_STUDENTS_BY_COURSE_NAME_SHORT)) {
                String courseName = parseSingleStringValue(line, OPT_FIND_STUDENTS_BY_COURSE_NAME_SHORT);
                findStudentsByCourseName(courseName);
            } else if (line.hasOption(OPT_ADD_NEW_STUDENT_SHORT)) {
                var studentName = line.getOptionValues(OPT_ADD_NEW_STUDENT_SHORT);
                addNewStudent(studentName[0], studentName[1]);
            } else if (line.hasOption(OPT_DELETE_STUDENT_BY_ID_SHORT)) {
                deleteStudentById(parseSingleIntValue(line, OPT_DELETE_STUDENT_BY_ID_SHORT));
            } else if (line.hasOption(OPT_ADD_STUDENT_TO_COURSE_SHORT)) {
                var ids = parseIntValues(line, OPT_ADD_STUDENT_TO_COURSE_SHORT);
                addStudentToCourse(ids[0], ids[1]);
            } else if (line.hasOption(OPT_REMOVE_STUDENT_FROM_COURSE_SHORT)) {
                var ids = parseIntValues(line, OPT_ADD_STUDENT_TO_COURSE_SHORT);
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
        
        var stringValues = line.getOptionValues(OPT_ADD_STUDENT_TO_COURSE_SHORT);
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

        options.addOption(OPT_FIND_GROUPS);
        options.addOption(OPT_FIND_STUDENTS_BY_COURSE_NAME);
        options.addOption(OPT_ADD_NEW_STUDENT);
        options.addOption(OPT_DELETE_STUDENT_BY_ID);
        options.addOption(OPT_ADD_STUDENT_TO_COURSE);
        options.addOption(OPT_REMOVE_STUDENT_FROM_COURSE);
        options.addOption(quitFromCLI);

        return options;
    }

}
