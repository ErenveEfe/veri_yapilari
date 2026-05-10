package com.library.logic;

import java.util.Stack;

// ekranlar arası geçiş geçmişini stack içinde tutar
public class NavigationManager {
    private Stack<String> sceneStack;

    public NavigationManager() {
        sceneStack = new Stack<>();
    }

    // gidilen son sayafayı yığıta ekler
    public void push(String state) {
        sceneStack.push(state);
    }

    // stackten bir önceki ekranı çıkarıp getirir
    public String pop() {
        if (!sceneStack.isEmpty()) {
            return sceneStack.pop();
        }
        return null;
    }

}
