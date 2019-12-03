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

    private static final String INSERT = "INSERT INTO groups (group_id, name) VALUES (?, ?)";
    private static final int INSERT_GROUP_ID_ARG_NUMBER = 1;
    private static final int INSERT_NAME_ARG_NUMBER = 2;

    private static final String FIND_BY_STUDENTS =
            "SELECT groups.group_id, groups.name, COUNT(students.student_id) " +
            "FROM groups " + 
            "LEFT JOIN students " + 
            "ON students.group_id = groups.group_id " + 
            "GROUP BY groups.group_id " + 
            "HAVING COUNT(*) <= ?" +
            "ORDER BY groups.group_id";
    private static final int FIND_BY_STUDENTS_COUNT_ARG_NUMBER = 1;


    private final static Logger logger = Logger.getLogger(GroupDaoJdbc.class);

    @Override
    public void saveAll(List<Group> groups) {

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ConnectionUtils.getConnection();
        } catch (Exception e) {
            logger.error("Cannot establish connection", e);
            return;
        }

        try {
            statement = connection.prepareStatement(INSERT);
            for (Group group : groups) {
                statement.setInt(INSERT_GROUP_ID_ARG_NUMBER, group.getId());
                statement.setString(INSERT_NAME_ARG_NUMBER, group.getName());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            logger.error("Cannot save groups", e);
        } finally {
            ConnectionUtils.closeQuietly(statement);
            ConnectionUtils.closeQuietly(connection);
        }
    }

    @Override
    public List<Group> findByStudentsCountsLessEqual(int count) {

        List<Group> groups = new ArrayList<Group>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionUtils.getConnection();
        } catch (Exception e) {
            logger.error("Cannot establish connection", e);
            return groups;
        }

        try {
            statement = connection.prepareStatement(FIND_BY_STUDENTS);
            statement.setInt(FIND_BY_STUDENTS_COUNT_ARG_NUMBER, count);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                groups.add(new Group(resultSet.getInt(1), resultSet.getString(2)));
            }
        } catch (SQLException e) {
            logger.error("Cannot save group", e);
        } finally {
            ConnectionUtils.closeQuietly(resultSet);
            ConnectionUtils.closeQuietly(statement);
            ConnectionUtils.closeQuietly(connection);
        }

        return groups;
    }

}
