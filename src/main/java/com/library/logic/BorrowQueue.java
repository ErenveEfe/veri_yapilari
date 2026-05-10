package com.library.logic;

import com.library.model.User;
import com.library.model.UserType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

// kitap bekleyenleri önceliğe göre sıraya dizer
public class BorrowQueue {
    private PriorityQueue<User> queue;

    public BorrowQueue() {

        Comparator<User> priorityComparator = new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {

                if (user1.getUserType() == UserType.ACADEMICIAN && user2.getUserType() != UserType.ACADEMICIAN) {
                    return -1;
                } else if (user1.getUserType() != UserType.ACADEMICIAN && user2.getUserType() == UserType.ACADEMICIAN) {
                    return 1;
                }
                return 0;
            }
        };

        queue = new PriorityQueue<>(priorityComparator);
    }

    // yeni kişiyi bekleme sırasına alır
    public boolean enqueue(User user) {
        for (User queuedUser : queue) {
            if (queuedUser.getId().equals(user.getId())) {
                return false;
            }
        }

        queue.offer(user);
        return true;
    }

    // sırası gelen en öncelikli kişiyi kuyruktan çıkartır
    public User dequeue() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    // listeyi arayüzde basmak için arraye çevirir
    public List<User> getQueueList() {
        List<User> list = new ArrayList<>();
        PriorityQueue<User> copy = new PriorityQueue<>(queue);
        while (!copy.isEmpty()) {
            list.add(copy.poll());
        }
        return list;
    }
}
