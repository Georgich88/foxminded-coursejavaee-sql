package com.foxminded.rodin.courses.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.foxminded.rodin.courses.dao.GroupDao;
import com.foxminded.rodin.courses.dao.QueryExecutor;
import com.foxminded.rodin.courses.dao.StudentDao;
import com.foxminded.rodin.courses.domain.Group;
import com.foxminded.rodin.courses.domain.Student;

public class GroupDaoJdbcTest {

    @BeforeEach
    public void initiateDatabaseBeforeTests() {
        QueryExecutor.createUser();
        QueryExecutor.createDatabase();
        QueryExecutor.createTables();
    }

    @Test
    public void shouldfindByStudentsCountsLessEqual() {
        
        GroupDao groupDao = new GroupDaoJdbc();
        StudentDao studentDao = new StudentDaoJdbc();

        List<Student> students = new ArrayList<Student>(4);
        students.add(new Student(0, "John", "Jones"));
        students.add(new Student(1, "Israel", "Adesanya"));
        students.add(new Student(2, "Stipe", "Miocic"));
        students.add(new Student(3, "Kamaru", "Usman"));

        List<Group> groups = new ArrayList<Group>(4);
        groups.add(new Group(0, "AA-11"));
        groups.add(new Group(1, "AA-12"));
        groups.add(new Group(2, "AB-22"));
        groups.add(new Group(3, "BC-45"));
        
        students.get(0).setGroupId(0);
        students.get(1).setGroupId(0);
        students.get(2).setGroupId(0);
        students.get(3).setGroupId(0);

        String expectedResult = "[Group [id=1, name=AA-12], Group [id=2, name=AB-22], Group [id=3, name=BC-45]]";
        groupDao.saveAll(groups);
        studentDao.saveAll(students);
        
        List<Group> savedGroups = groupDao.findByStudentsCountsLessEqual(2);

        assertEquals(expectedResult, savedGroups.toString());
    }
}
