package com.library.logic;

import com.library.model.User;
import com.library.model.UserType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class BorrowQueue {
    // Öncelikli Kuyruk (Priority Queue) ve arka planda Heap yapısı kullanıyoruz
    private PriorityQueue<User> queue;

    public BorrowQueue() {
        // Akademisyenlerin önceliği var. Akademisyen ise başa, değilse sona atan bir kural yazdık.
        // Bu Comparator, PriorityQueue'nun Heap (Ağaç tabanlı öncelik) mantığıyla sıralama yapmasını sağlar.
        Comparator<User> priorityComparator = new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                // Eğer 1. kullanıcı akademisyen, 2. öğrenci ise 1. öne geçer (-1)
                if (user1.getUserType() == UserType.ACADEMICIAN && user2.getUserType() != UserType.ACADEMICIAN) {
                    return -1; 
                } 
                // Eğer 2. kullanıcı akademisyen, 1. öğrenci ise 2. öne geçer (1)
                else if (user1.getUserType() != UserType.ACADEMICIAN && user2.getUserType() == UserType.ACADEMICIAN) {
                    return 1;  
                }
                return 0; // İkisi de aynıysa öncelik eşit
            }
        };
        
        queue = new PriorityQueue<>(priorityComparator);
    }

    public boolean enqueue(User user) {
        // Kuyrukta zaten var mı diye kontrol edelim
        for (User queuedUser : queue) {
            if (queuedUser.getId().equals(user.getId())) {
                return false;
            }
        }
        
        queue.offer(user);
        return true;
    }

    public User dequeue() {
        // En yüksek öncelikli olanı (Heap'in tepesindekini) çıkartıp verir (O(log n))
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public List<User> getQueueList() {
        // Arayüzde göstermek için listeye çevirip döndürüyoruz
        List<User> list = new ArrayList<>();
        PriorityQueue<User> copy = new PriorityQueue<>(queue);
        while (!copy.isEmpty()) {
            list.add(copy.poll());
        }
        return list;
    }
}
