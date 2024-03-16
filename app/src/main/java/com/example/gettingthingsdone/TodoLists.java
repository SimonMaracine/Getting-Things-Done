package com.example.gettingthingsdone;

import java.util.HashMap;

public class TodoLists {
    private final HashMap<Integer, TodoList> lists = new HashMap<>();
    private int counter;

    int add(TodoList list) {
        lists.put(counter, list);

        return counter++;
    }

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
}
