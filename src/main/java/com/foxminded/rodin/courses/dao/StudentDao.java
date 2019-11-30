package com.foxminded.rodin.courses.dao;

import java.util.List;
import java.util.Map;

import com.foxminded.rodin.courses.domain.Course;
import com.foxminded.rodin.courses.domain.Student;

public interface StudentDao {

    List<Student> findAll();

    void saveAll(List<Student> students);

    void save(Student student);

    void deleteById(int studentId);

    List<Student> findByCourseName(String courseName);

    void assignToCourses(Map<Student, List<Course>> map);

    void assignToCourse(int studentId, int courseId);

    void deleteFromCourse(int studentId, int courseId);

}
