package com.foxminded.rodin.courses.dao;

import java.util.List;

import com.foxminded.rodin.courses.domain.Course;

public interface CourseDao {

    void saveAll(List<Course> courses);

    List<Course> findAll();

    List<Course> findByStudentId(int studentId);
}
