package com.foxminded.rodin.courses.dao;

import java.util.List;

import com.foxminded.rodin.courses.domain.Student;

public interface StudentDao {

    List<Student> findAll();

    void saveAll(List<Student> students);

    void save(Student student);

    void delete(int studentId);

    List<Student> findByCourseName(String courseName);

    void assignToCourse(int studentId, int courseId);

    void deleteFromCourse(int studentId, int courseId);

}
