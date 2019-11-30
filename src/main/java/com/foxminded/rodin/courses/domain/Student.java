package com.foxminded.rodin.courses.domain;

public class Student {

    private int id;
    private int group_id;
    private String firstName;
    private String lastName;

    public Student(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return group_id;
    }

    @Override
    public String toString() {
        return "Student [id=" + id + ", group_id=" + group_id + ", firstName=" + firstName + ", lastName=" + lastName
                + "]";
    }

    public void setGroupId(int i) {
        this.group_id = i;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
