package com.foxminded.rodin.courses.dao;

import java.util.List;

import com.foxminded.rodin.courses.domain.Group;

public interface GroupDao {

    void saveAll(List<Group> groups);

    List<Group> findByStudentsCountsLessEqual(int count);

}
