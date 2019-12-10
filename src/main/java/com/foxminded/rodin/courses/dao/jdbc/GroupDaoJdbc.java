package com.foxminded.rodin.courses.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.foxminded.rodin.courses.dao.ConnectionUtils;
import com.foxminded.rodin.courses.dao.GroupDao;
import com.foxminded.rodin.courses.domain.Group;

public class GroupDaoJdbc implements GroupDao {

    private static final String INSERTION_QUERY_TEMPLATE = "INSERT INTO groups (group_id, name) VALUES (?, ?)";
    private static final int INSERTION_GROUP_ID_PARAMETER = 1;
    private static final int INSERTION_NAME_PARAMETER = 2;

    private static final String SELECTION_BY_STUDENTS_QUERY_TEMPLATE =
            "SELECT groups.group_id, groups.name, COUNT(students.student_id) " +
            "FROM groups " + 
            "LEFT JOIN students " + 
            "ON students.group_id = groups.group_id " + 
            "GROUP BY groups.group_id " + 
            "HAVING COUNT(*) <= ?" +
            "ORDER BY groups.group_id";
    private static final int SELECTION_BY_STUDENTS_COUNT_PARAMETER = 1;


    private final static Logger logger = Logger.getLogger(GroupDaoJdbc.class);

    @Override
    public void saveAll(List<Group> groups) {

        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERTION_QUERY_TEMPLATE);) {
            for (Group group : groups) {
                statement.setInt(INSERTION_GROUP_ID_PARAMETER, group.getId());
                statement.setString(INSERTION_NAME_PARAMETER, group.getName());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            logger.error("Cannot save groups", e);
        }
    }

    @Override
    public List<Group> findByStudentsCountsLessEqual(int count) {

        List<Group> groups = new ArrayList<Group>();

        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECTION_BY_STUDENTS_QUERY_TEMPLATE);) {
            statement.setInt(SELECTION_BY_STUDENTS_COUNT_PARAMETER, count);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                groups.add(new Group(resultSet.getInt(1), resultSet.getString(2)));
            }
        } catch (SQLException e) {
            logger.error("Cannot save group", e);
        }

        return groups;
    }

}
