package com.saqi.time_scheduler.Utils;

import com.saqi.time_scheduler.Models.AssignTeacherToClass;

import java.util.ArrayList;
import java.util.List;

public class QueueForAssignTeachers {
    private List<AssignTeacherToClass> data;
    private int start = -1;
    private int end = -1;


    public QueueForAssignTeachers(List<AssignTeacherToClass> data) {
        this.data = data;
        end = data.size() - 1;
        start = -1;
    }

    public QueueForAssignTeachers() {
        this.data = new ArrayList<>();
        start = -1;
        end = -1;
    }

    public int size() {
        return (end - start);
    }

    public void clear() {
        data.clear();
        start = -1;
        end = -1;
    }

    public boolean isEmpty() {
        return start == end;
    }


    public void push(AssignTeacherToClass assignTeacherToClass) {
        end++;
        data.add(end, assignTeacherToClass);
    }

    public AssignTeacherToClass pop() {
        if (isEmpty()) {
            return null;
        }
        start++;
        return this.data.get(start);
    }
}
