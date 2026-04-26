package com.library.logic;

import java.util.Stack;

public class NavigationManager {
    private Stack<String> sceneStack;

    public NavigationManager() {
        sceneStack = new Stack<>();
    }

    public void push(String state) {
        sceneStack.push(state);
    }

    public String pop() {
        if (!sceneStack.isEmpty()) {
            return sceneStack.pop();
        }
        return null;
    }

    public String peek() {
        if (!sceneStack.isEmpty()) {
            return sceneStack.peek();
        }
        return null;
    }

    public boolean isEmpty() {
        return sceneStack.isEmpty();
    }
}
