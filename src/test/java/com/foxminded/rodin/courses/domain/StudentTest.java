package com.foxminded.rodin.courses.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class StudentTest {

    @Test
    public void shouldCreateValidStudentList() {

        List<Student> students = new ArrayList<Student>(4);
        students.add(new Student(0, "John", "Jones"));
        students.add(new Student(1, "Israel", "Adesanya"));
        students.add(new Student(2, "Stipe", "Miocic"));
        students.add(new Student(3, "Kamaru", "Usman"));

        String expectedResult = "[Student [id=0, group_id=0, firstName=John, lastName=Jones], Student [id=1, group_id=0, firstName=Israel, lastName=Adesanya], Student [id=2, group_id=0, firstName=Stipe, lastName=Miocic], Student [id=3, group_id=0, firstName=Kamaru, lastName=Usman]]";

        assertEquals(expectedResult, students.toString());
    }

    @Test
    public void shouldChangeStudentIdAndName() {

        Student student = new Student(0, "Daniel", "Cormier");
        student.setId(1);
        student.setFirstName("John");
        student.setLastName("Jones");

        assertEquals(1, student.getId());
        assertEquals("John", student.getFirstName());
        assertEquals("Jones", student.getLastName());
    }

}
