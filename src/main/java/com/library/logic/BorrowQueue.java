package com.library.logic;

import com.library.model.User;
import com.library.model.UserType;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList;

public class BorrowQueue {
    private Queue<User> academicianQueue;
    private Queue<User> studentQueue;

    public BorrowQueue() {
        academicianQueue = new LinkedList<>();
        studentQueue = new LinkedList<>();
    }

    public boolean enqueue(User user) {
        if (academicianQueue.stream().anyMatch(u -> u.getId().equals(user.getId())) ||
            studentQueue.stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            return false;
        }
        
        if (user.getUserType() == UserType.ACADEMICIAN) {
            academicianQueue.offer(user);
        } else {
            studentQueue.offer(user);
        }
        return true;
    }

    public User dequeue() {
        if (!academicianQueue.isEmpty()) {
            return academicianQueue.poll();
        }
        return studentQueue.poll();
    }

    public boolean isEmpty() {
        return academicianQueue.isEmpty() && studentQueue.isEmpty();
    }

    public List<User> getQueueList() {
        List<User> list = new ArrayList<>();
        list.addAll(academicianQueue);
        list.addAll(studentQueue);
        return list;
    }
}
