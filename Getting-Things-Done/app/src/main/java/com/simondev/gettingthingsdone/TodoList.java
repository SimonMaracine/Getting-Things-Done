package com.simondev.gettingthingsdone;

import androidx.annotation.NonNull;

import java.util.Iterator;
import java.util.TreeMap;

class TodoList implements Iterable<TodoTask> {
    final int index;
    private String name;
    private final TreeMap<Integer, TodoTask> tasks = new TreeMap<>();
    private int counter;

    TodoList(int index, String name) {
        this.index = index;
        this.name = name;
    }

    int addTask(String title) {
        TodoTask task = new TodoTask(counter);
        task.content = title;

        tasks.put(counter, task);

        return counter++;
    }

    void removeTask(int index) {
        tasks.remove(index);
    }

    TodoTask getTask(int i) {
        return tasks.get(i);
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    int getCounter() {
        return counter;
    }

    @NonNull
    @Override
    public Iterator<TodoTask> iterator() {
        return tasks.values().iterator();
    }
}
