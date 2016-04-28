package com.tacostudios.apps.bogey;

public class CourseList {
    public String[] names;
    public String[] desc;
    public int[]    courseID;

    private int num_courses;

    public void initialize(int courses) {
        num_courses = courses;

        names = new String[num_courses];
        desc = new String[num_courses];
        courseID = new int[num_courses];
    }

    public int length() {
        return num_courses;
    }

    public int getID(int position) {
        return courseID[position];
    }
}
