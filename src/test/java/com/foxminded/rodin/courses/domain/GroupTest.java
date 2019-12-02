package com.foxminded.rodin.courses.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class GroupTest {

    @Test
    public void shouldCreateValidGroups() {

        List<Group> groups = new ArrayList<Group>(4);
        groups.add(new Group(0, "AA-11"));
        groups.add(new Group(1, "AA-12"));
        groups.add(new Group(2, "AB-22"));
        groups.add(new Group(3, "BC-45"));

        String expectedResult = "[Group [id=0, name=AA-11], Group [id=1, name=AA-12], Group [id=2, name=AB-22], Group [id=3, name=BC-45]]";

        assertEquals(expectedResult, groups.toString());
    }

    @Test
    public void shouldChangeGroupIdAndName() {

        Group group = new Group(0, "AA-11");
        group.setId(2);
        group.setName("ZZ-01");

        assertEquals(2, group.getId());
        assertEquals("ZZ-01", group.getName());
    }

}
