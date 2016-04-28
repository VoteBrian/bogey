package com.tacostudios.apps.bogey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CourseAdapter extends BaseAdapter {
    Context ctx;
    DbAdapter adapter;
    LayoutInflater inflater;

    TextView courseName;
    TextView courseDesc;

    CourseList courseList;
    int num_courses;

    public CourseAdapter(Context context, DbAdapter db) {
        ctx = context;
        adapter = db;

        inflater = LayoutInflater.from(ctx);
        courseList = db.getCourseList();
    }

    public int getCount() {
        return courseList.length();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.course_row, parent, false);

        courseName = (TextView) convertView.findViewById(R.id.course_row_name);
        courseDesc = (TextView) convertView.findViewById(R.id.course_row_desc);

        courseName.setText(courseList.names[position]);
        courseDesc.setText(courseList.desc[position]);

        return convertView;
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return position;
    }

    public int getCourseID(int position) {
        return courseList.getID(position);
    }
}
