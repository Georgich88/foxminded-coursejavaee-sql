package com.foxminded.rodin.courses.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CourseTest {

    @Test
    public void shouldCreateCourses() {

        List<Course> courses = new ArrayList<Course>(4);
        courses.add(new Course(0, "Math", "Mathematics"));
        courses.add(new Course(1, "Biology", "Anatomy, biophysics, cell and molecular biology, computational biology"));
        courses.add(new Course(2, "Literature",
                "Main subjects of courses in literature include: cultural and literature, "
                        + "topics in literary research, reading fiction, poetry, modern drama, classical literature, women's literature. "));
        courses.add(new Course(3, "Physics",
                "Challenge because many a concept in Physics are challenging and can strain your cognitive tissues"));

        String expectedResult = "[Course [id=0, name=Math, description=Mathematics, students=[]], "
                + "Course [id=1, name=Biology, description=Anatomy, biophysics, cell and molecular biology, computational biology, students=[]], "
                + "Course [id=2, name=Literature, description=Main subjects of courses in literature include: cultural and literature, topics in literary research, reading fiction, poetry, modern drama, classical literature, women's literature. , students=[]], "
                + "Course [id=3, name=Physics, description=Challenge because many a concept in Physics are challenging and can strain your cognitive tissues, students=[]]]";

        assertEquals(expectedResult, courses.toString());
    }

    @Test
    public void shouldChangeCourseFields() {

        Course group = new Course(0, "Data science",
                "Data science is a multi-disciplinary field that uses scientific methods, "
                        + "processes, algorithms and systems to extract knowledge "
                        + "and insights from structured and unstructured data.");
        group.setId(2);
        group.setName("Math");
        group.setDescription("Mathematics");

        assertEquals(2, group.getId());
        assertEquals("Math", group.getName());
        assertEquals("Mathematics", group.getDescription());
    }


}
