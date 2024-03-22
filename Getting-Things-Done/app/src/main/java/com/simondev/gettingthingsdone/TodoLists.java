package com.simondev.gettingthingsdone;

import androidx.annotation.NonNull;

import java.util.Iterator;
import java.util.TreeMap;

class TodoLists implements Iterable<TodoList> {
    private final TreeMap<Integer, TodoList> lists = new TreeMap<>();
    private int counter;

    int add(String name) {
        lists.put(counter, new TodoList(counter, name));

        return counter++;
    }

    TodoList get(int index) {
        return lists.get(index);
    }

    int getCounter() {
        return counter;
    }

    @NonNull
    @Override
    public Iterator<TodoList> iterator() {
        return lists.values().iterator();
    }
}
