package com.simondev.gettingthingsdone;

import java.util.ArrayList;

class TodoList {
    private final int index;
    private String name;
    private final ArrayList<TodoTask> tasks = new ArrayList<>();

    TodoList(int index, String name) {
        this.index = index;
        this.name = name;
    }

    int addTask(String title) {
        TodoTask task = new TodoTask();
        task.content = title;

        tasks.add(task);

        return tasks.size() - 1;
    }

    TodoTask getTask(int i) {
        return tasks.get(i);
    }

    int getIndex() {
        return index;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    int getCount() {
        return tasks.size();
    }
}
