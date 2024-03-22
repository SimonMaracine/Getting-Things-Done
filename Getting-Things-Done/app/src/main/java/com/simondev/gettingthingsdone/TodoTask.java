package com.simondev.gettingthingsdone;

public class TodoTask {
    final int index;
    String content;
    boolean done = false;

    TodoTask(int index) {
        this.index = index;
    }
}
