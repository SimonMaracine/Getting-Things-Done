package com.example.gettingthingsdone;

import java.util.ArrayList;

class TodoList {
    int id;
    String name;
    ArrayList<TodoTask> tasks;

    TodoList(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
